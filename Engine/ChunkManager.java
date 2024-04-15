package Kartoffel.Licht.Engine;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import Kartoffel.Licht.Java.freeable;

/**
 * The ChunkManager class is a simple implementation of a chunk manager.
 * @param <T> the chunk object 
 * @param <L> the chunk data object (generated in other thread)
 */
public abstract class ChunkManager<T, L> implements freeable{
	
	final protected List<node> nodes = new ArrayList<node>();
	final protected List<node> building = new ArrayList<node>();
	final private int[] current_global = new int[3];
	/**
	 * the area that should be checked. All empty spaces <= SCAN_SIZE and >= -SCAN_SIZE relative to he middle chunk will be checked.
	 */
	final public int[] SCAN_SIZE = new int[] {10, 10, 10};
	public ExecutorService s = Executors.newSingleThreadExecutor();
	
	/**
	 * Updates the chunk manager. When called, all chunks will be checked and, if necessary, (re)created/deleted.<br>
	 * 
	 * 
	 * @param global_x X coordinate of the middle chunk* <br>
	 * @param global_y Y coordinate of the middle chunk* <br>
	 * @param global_z Z coordinate of the middle chunk* <br>
	 * * (i.e. the chunk, in which the player is in)
	 */
	final public void update(int global_x, int global_y, int global_z) {
		current_global[0] = global_x;
		current_global[1] = global_y;
		current_global[2] = global_z;
		for(int i = 0; i < building.size(); i++) { //Look if node in building is already build
			node n = building.get(i);
			//Check if chunk is build
			if(n.chunkData instanceof Future && n.chunkData != null) {
				if(n.chunkData.isDone())
					try {
						n.chunk = finish(n.chunkData.get(), n.x, n.y, n.z, n.lod);
						n.chunkData = null;
						node old = getNode(n.x, n.y, n.z); //Remove old node
						if(old != null) {
							destroy(old.chunk);
							nodes.remove(old);
						}
						building.remove(n); //Move finished nodes to main List
						nodes.add(n);
					} catch (InterruptedException | ExecutionException e) {
						System.err.println("Failed to finish chunk! " + n + " : " + e.getLocalizedMessage());
					}
			}
		}
		for(int i = 0; i < nodes.size(); i++) { //Updating and Destroying node
			node n = nodes.get(i);
			
			int l = getLOD(global_x-n.x, global_y-n.y, global_z-n.z, n.x, n.y, n.z);
			if(l != n.lod) {
				if(n.chunk != null) { //Only modify node when its build
					//Delete node
					if(l >= 0) { //If lod l >= 0, then create new node. Old node will be destroyed when the new one has finished building
						if(getBuildingNode(n.x, n.y, n.z) == null) //Check if new node is not already building
							addNode(n.x, n.y, n.z, l);
					}
					else { //Delete node when lod < 0 -> no replacement node
						destroy(n.chunk);
						nodes.remove(n);
					}
				}
			}
		}
		
		for(int xx = -SCAN_SIZE[0]; xx <= SCAN_SIZE[0]; xx++) //Creating new nodes
			for(int yy = -SCAN_SIZE[1]; yy <= SCAN_SIZE[1]; yy++)
				for(int zz = -SCAN_SIZE[2]; zz <= SCAN_SIZE[2]; zz++) {
					int l = getLOD(xx, yy, zz, global_x-xx, global_y-yy, global_z-zz);
					if(l >= 0)
						if(getNode(global_x-xx, global_y-yy, global_z-zz) == null && getBuildingNode(global_x-xx, global_y-yy, global_z-zz) == null) {
							addNode(global_x-xx, global_y-yy, global_z-zz, l);
						}
				}
		
	}
	
	@Override
	public void free() {
		s.shutdownNow();
		for(node n : nodes)
			destroy(n.chunk);
	}
	
	final private void addNode(int a, int b, int c, int d) {
		building.add(new node(a, b, c, d, s.submit(new Callable<L>() {
			
			@Override
			public L call() throws Exception {
				try {
					return build(a, b, c, d);
				} catch (Exception e) {
					System.err.println("Error while building!");
					throw e;
				}
			}
			
		})));
	}
 	
	/**
	 * returns the node at this coordinate.
	 * @param gx X coordinate of the node
	 * @param gy Y coordinate of the node
	 * @param gz Z coordinate of the node
	 * @return the node or null, if no node was found
	 */
	final public node getNode(int gx, int gy, int gz) {
		for(int i = 0; i < nodes.size(); i++)
			if(nodes.get(i).x == gx && nodes.get(i).y == gy && nodes.get(i).z == gz)
				return nodes.get(i);
		return null;
	}
	/**
	 * returns the building node at this coordinate.
	 * @param gx X coordinate of the node
	 * @param gy Y coordinate of the node
	 * @param gz Z coordinate of the node
	 * @return the node or null, if no node was found
	 */
	final public node getBuildingNode(int gx, int gy, int gz) {
		for(int i = 0; i < building.size(); i++)
			if(building.get(i).x == gx && building.get(i).y == gy && building.get(i).z == gz)
				return building.get(i);
		return null;
	}
	/**
	 * calculates the LOD of a chunk, given its coordinates
	 * @param dx X coordinate relative to the middle chunk
	 * @param dy Y coordinate relative to the middle chunk
	 * @param dz Z coordinate relative to the middle chunk
	 * @param gx X coordinate of the chunk
	 * @param gy Y coordinate of the chunk
	 * @param gz Z coordinate of the chunk
	 * @return the LOD. Generally should decrease with chunk quality. Negative values mean no chunk should be placed at the given coordinate
	 */
	public abstract int getLOD(int dx, int dy, int dz, int gx, int gy, int gz);
	/**
	 * Builds the chunk. This function will be run on a separate thread.
	 * @param gx X coordinate of the chunk
	 * @param gy Y coordinate of the chunk
	 * @param gz Z coordinate of the chunk
	 * @param lod LOD of the chunk
	 * @return a user defined object containing the chunk data
	 */
	public abstract L build(int gx, int gy, int gz, int lod);
	/**
	 * Finishes the chunk. This function will be run when the chunk is build.
	 * @param chunkData user defined object containing the chunk data
	 * @param gx X coordinate of the chunk
	 * @param gy Y coordinate of the chunk
 	 * @param gz Z coordinate of the chunk
	 * @param l LOD of the chunk
	 * @return a user defined chunk
	 */
	public abstract T finish(L chunkData, int gx, int gy, int gz, int l);
	/**
	 * Destroys the chunk.
	 * @param object user defined Chunk
	 */
	public abstract void destroy(T object);

	/**
	 * @return a list containing all active chunks
	 */
	final public List<node> getChunks() {
		return nodes;
	}
	/**
	 * @return a list containing all currently building chunks
	 */
	final public List<node> getBuilding() {
		return building;
	}
	
	public class node {
		public Future<L> chunkData;
		public T chunk;
		public final int x, y, z, lod;
		node(int x, int y, int z, int lod, Future<L> future) {
			this.chunkData = future;
			this.x = x;
			this.y = y;
			this.z = z;
			this.lod = lod;
		}
		@Override
		public String toString() {
			return "["+x+"."+y+"."+z+";"+lod+"]";
		}
	}
}
