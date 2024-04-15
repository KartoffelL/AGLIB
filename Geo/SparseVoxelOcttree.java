package Kartoffel.Licht.Geo;

import Kartoffel.Licht.Rendering.Shaders.Objects.ShaderStorage;
import Kartoffel.Licht.Tools.Tools;

public class SparseVoxelOcttree {
	
	public static interface voxelProvider {
		public int generate(float x, float y, float z, float size, int subdiv, boolean solid);
		public boolean isSolid(float x, float y, float z, float size, int subdiv);
	}

	public static class VoxelCollapser implements voxelProvider{
		public int MAX_SUBDIVS;
		private int[][][] field;
		private int amount;
		public VoxelCollapser(int[][][] data) {
			this.MAX_SUBDIVS = (int)(Math.log(data.length) / Math.log(2));;
			this.amount = data.length;
			this.field = data;
			for(int i = 0; i < amount; i++) {
				for(int l = 0; l < amount; l++) {
					for(int k = 0; k < amount; k++) {
						this.field[i][l][k] = get((float)i/amount, (float)l/amount, (float)k/amount);
					}
				}
			}
		}
		public int get(float x, float y, float z) {
			return y < x ? 1 : 0;
		}
		public int generate(float x, float y, float z, float size, int subdiv, boolean solid) {
			if(!solid)
				return 1;
			return field[(int) (x*amount)][(int) (y*amount)][(int) (z*amount)];
		}
		public boolean isSolid(float x, float y, float z, float size, int subdiv) {
			if(subdiv >= MAX_SUBDIVS)
				return true;
			int checks = (int) (amount*size);
			int type = field[(int) (x*amount)][(int) (y*amount)][(int) (z*amount)];
			boolean solid = true;
			for(int i = 0; i < checks; i++) {
				for(int l = 0; l < checks; l++) {
					for(int k = 0; k < checks; k++) {
						if(type != field[(int) (x*amount+i)][(int) (y*amount+l)][(int) (z*amount+k)]) {
							solid =  false;
						}
					}
				}
			}
			return solid;
		}
	}
	
	public static abstract class DynamicVoxelProvider implements voxelProvider{
		public int subdivs;
		public int MAX_SUBDIVS = 21;
		public int MAX_CACHE_SIZE = 128; //
		public int COLLAPS_MIN_SUBDIVS = 2;
		private int amount;
		public DynamicVoxelProvider(int detail) {
			this.subdivs = detail;
			this.amount = (int) Math.pow(2, Math.min(MAX_SUBDIVS, subdivs));
			this.cache = new byte[0][0][0];
		}
		public abstract int get(float x, float y, float z);
		public int generate(float x, float y, float z, float size, int subdiv, boolean solid) {
			if(!solid)
				return 1;
			return get(x, y, z);
		}
		private byte[][][] cache;
		private float[] cachePos = new float[] {-1, -1, -1, -1}; //x, y, z, size
		public void generateCache(float size, byte[][][] cache, float x, float y, float z) {
			for(float i = 0; i < size; i+=1.0/amount) {
				for(float l = 0; l < size; l+=1.0/amount) {
					for(float k = 0; k < size; k+=1.0/amount) {
						cache[(int) (i*amount)][(int) (l*amount)][(int) (k*amount)] = (byte) get(x+i, y+l, z+k);
					}
				}
			}
		}
		public boolean isSolid(float x, float y, float z, float size, int subdiv) {
			if(subdiv >= Math.min(MAX_SUBDIVS, subdivs))
				return true;
			if(subdiv < COLLAPS_MIN_SUBDIVS)
				return false;
			int type = get(x, y, z);
			boolean solid = true;
			boolean cacheUsable = 
					  (x >= cachePos[0] && (x+size) <= (cachePos[0]+cachePos[3]))
					&&(y >= cachePos[1] && (y+size) <= (cachePos[1]+cachePos[3]))
					&&(z >= cachePos[2] && (z+size) <= (cachePos[2]+cachePos[3]));
			
			if(size*amount > MAX_CACHE_SIZE) { //Default
				for(float i = 0; i < size; i+=1.0/amount) {
					for(float l = 0; l < size; l+=1.0/amount) {
						for(float k = 0; k < size; k+=1.0/amount) {
							if(type != get(x+i, y+l, z+k)) {
								solid = false;
							}
						}
					}
				}
			}
			else { //Using Cache
				if(!cacheUsable) {//Cache has to be recreated
					if(cache.length != (int) (size*amount)) //Only reallocate if necessary
						cache = new byte[(int) (size*amount)][(int) (size*amount)][(int) (size*amount)];
					cachePos[0] = x; 
					cachePos[1] = y; 
					cachePos[2] = z; 
					cachePos[3] = size;
					generateCache(size, cache, x, y, z);
				}
				float offX = x-cachePos[0];
				float offY = y-cachePos[1];
				float offZ = z-cachePos[2];
				for(float i = 0; i < size; i+=1.0/amount) {
					for(float l = 0; l < size; l+=1.0/amount) {
						for(float k = 0; k < size; k+=1.0/amount) {
							if(type != cache[(int) ((i+offX)*amount)][(int) ((l+offY)*amount)][(int) ((k+offZ)*amount)]) {
								solid = false;
							}
						}
					}
				}
			
			}
			return solid;
		}
	}
	
	final public static int OCTAT_SIZE = 9; //Color + 8IDS
	final public static int[][] OFFSET = new int[][]{{0, 0, 0},{1, 0, 0},{0, 1, 0},{1, 1, 0},{0, 0, 1},{1, 0, 1},{0, 1, 1},{1, 1, 1}};
	public static int MAX_TREE_DEPTH = 65536;
	
	private float[] data;
	private boolean[] set;
	
	public SparseVoxelOcttree(float[] data, boolean[] set) {
		this.data = data;
		this.set = set;
	}
	public SparseVoxelOcttree(int amount, int init_population, voxelProvider provider) {
		data = new float[amount*OCTAT_SIZE];
		set = new boolean[amount];
		put(0, 1, 0, 0, 0, 0, 0, 0, 0, 0); //First one : )
		set[0] = true;
		generate(init_population, provider);
	}
	
	public void printData() {
		printElement(data, 0, 0);
	}
	private static void printElement(float[] data, int current, int m) {
		if(m > MAX_TREE_DEPTH)
			throw new RuntimeException("Tree depth is too big!");
		System.out.println(Tools.spacing(m)+"Color: " + data[current*OCTAT_SIZE+0]);
		for(int i = 0; i < 8; i++)
			if(data[current*OCTAT_SIZE+1+i] > 0)
				printElement(data, (int) data[current*OCTAT_SIZE+1+i], m+1);
	}
	public void generate(int maxElements, voxelProvider gen) {
		for(int i = 0; i < maxElements; i++) {
			float[] next = findNonComplete();
			if(next == null) //No more spots found!
				break;
			
			int type = getType((int) next[5], next[2], next[3], next[4], gen);
			int current = -1;
			if(type != 0) {
				current = findFree();
				if(current == -1) //No free memory anymore!
					return;
				putMaterial(current, type);
			}
			putAddress((int)next[0], (int)next[1], current);
		}
	}
	public int[] generateSingle(voxelProvider gen) {
		float[] next = findNonComplete();
		if(next == null) //No more spots found!
			return null;
		
		int type = getType((int) next[5], next[2], next[3], next[4], gen);
		int current = -1;
		if(type != 0) {
			current = findFree();
			if(current == -1) //No free memory anymore!
				return null;
			putMaterial(current, type);
		}
		putAddress((int)next[0], (int)next[1], current);
		return new int[] {(int) next[0], current};
	}
	public int getType(int subdiv, float x, float y, float z, voxelProvider gen) {
		float size = (float) (1/Math.pow(2, subdiv));
		boolean isSolid = gen.isSolid(x, y, z, size, subdiv);
		int type = gen.generate(x, y, z, size, subdiv, isSolid);
		return type*(isSolid ? -1 : 1);
	}
	public int traverseOctree(int...path) {
		int address = 0;
		for(int b : path) {
			address = (int) data[address*OCTAT_SIZE+1+b];
		}
		return address;
	}
	public float[] getRelativePosition(int...path) {
		float x = 0;
		float y = 0;
		float z = 0;
		for(int i = 0; i < path.length; i++) {
			float d = (float) (1.0/Math.pow(2, i+1));
			x += OFFSET[path[i]][0]*d;
			y += OFFSET[path[i]][1]*d;
			z += OFFSET[path[i]][2]*d;
		}
		return new float[] {x, y, z};
	}
	public int[] isHit(float x, float y, float z) {
		if(Math.abs(x-.5) > .5 || Math.abs(y-.5) > .5 || Math.abs(z-.5) > .5)
			return null;
		int current = 0; //Pointer to current octant
		for(int i = 0; i < MAX_TREE_DEPTH; i++) {
			float val = data[current+0]; //Current value of octant
			float l = (float) (1.0/Math.pow(2, i)); //Calculate scale of octant (1, 0.5, 0.25, ...)
			
			if(val <= 0) {//Octant is solid, no childs exist
				return new int[] {(int) Math.abs(val), i};
			}
			
			int index = 0;
			index += Tools.mod(x, l) < l*.5 ? 1 : 0; //Set index based on position
			index += Tools.mod(y, l) < l*.5 ? 2 : 0;
			index += Tools.mod(z, l) < l*.5 ? 4 : 0;
			current = (int) (data[current+1+index]*9); //set current to correct child-octant
			
			if(current == 0) { //Traversed to smallest LOD
				return new int[] {(int) Math.abs(val), i};
			}
			else if(current < 0) //Selected child node is air
				return null;
		}
		return null;
	}
	private int nearestIndex = 0;
	private int nearestIndex2 = 0;
	private long nearestIndex3 = 0;
	public int getNearestIndex() {
		return nearestIndex;
	}
	public int getNearestIndex2() {
		return nearestIndex2;
	}
	public float getNearestIndex3() {
		return nearestIndex3;
	}
	public int getRemaining() {
		int index = nearestIndex;
		int count = 0;
		while(index < set.length) {
			if(!set[index])
				count++;
			index++;
		}
		return count;
	}
	public int getSize() {
		return data.length/OCTAT_SIZE;
	}
	/**
	 * Creates a new octant
	 * @return
	 */
	public int findFree() {
		int index = nearestIndex;
		while(index < set.length) {
			if(!set[index]) {
				nearestIndex = index;
				set[index] = true;
				return index;
			}
			index++;
		}
		return -1;
	}
	/**
	 * Deletes an octant
	 * @param current
	 */
	public void delete(int current) {
		put(current, 0, 0, 0, 0, 0, 0, 0, 0, 0);
		set[current] = false;
		nearestIndex = current;
		nearestIndex2 = current;
	}
	public float[] findNonComplete() {
		return findNonCompleteElement(data, 0, 0, 0, 0, 0, 0);
	}
	
	private float[] findNonCompleteElement(float[] data, int current, int m, float x, float y, float z, long path) {
		int castPath = (int) ((nearestIndex3>>(60-m*3))&7l);
		if(current < 0 || data[current*OCTAT_SIZE+0] <= 0) //This child is air and has no children or  //Octant is solid or air (air octants cant have children)
			return null;
		else if(m > MAX_TREE_DEPTH)		//Checks
			throw new RuntimeException("Tree depth is too big!");
		
		float childSize = (float) (1/Math.pow(2, m+1)); //Calculate size for childsl
		for(int i = castPath; i < 8; i++) {
			float nx = x+OFFSET[i][0]*childSize;
			float ny = y+OFFSET[i][1]*childSize;
			float nz = z+OFFSET[i][2]*childSize;
			int childID = (int) data[current*OCTAT_SIZE+1+i];
			long npath = path|((long)i)<<(60l-m*3);//Excluding sign bit
			if(childID == 0) {//Sub is not set!
				nearestIndex3 = path;
				return new float[] {current, i, nx, ny, nz, m+1};
			}
			float[] res = findNonCompleteElement(data, childID, m+1, nx, ny, nz, npath);
			if(res != null)
				return res;
			
		}
		return null;
	}
	
	public void put(int index, int color, int addressA, int addressB, int addressC, int addressD, int addressE, int addressF, int addressG, int addressH) {
		data[index*OCTAT_SIZE+0] = color;
		data[index*OCTAT_SIZE+1] = addressA;
		data[index*OCTAT_SIZE+2] = addressB;
		data[index*OCTAT_SIZE+3] = addressC;
		data[index*OCTAT_SIZE+4] = addressD;
		data[index*OCTAT_SIZE+5] = addressE;
		data[index*OCTAT_SIZE+6] = addressF;
		data[index*OCTAT_SIZE+7] = addressG;
		data[index*OCTAT_SIZE+8] = addressH;
		nearestIndex2 = index;
	}
	public void putAddress(int index, int addressIndex, int address) {
		data[index*OCTAT_SIZE+1+addressIndex] = address;
		nearestIndex2 = index;
	}
	public void putMaterial(int index, int color) {
		data[index*OCTAT_SIZE+0] = color;
		nearestIndex2 = index;
	}
	private float[] buff = new float[OCTAT_SIZE];
	public void updateInstance(ShaderStorage ssbo, int instance, boolean updateChildrenRec) {
		System.arraycopy(data, instance*OCTAT_SIZE, buff, 0, OCTAT_SIZE);
		ssbo.updateSubData(buff, instance*OCTAT_SIZE);
		if(updateChildrenRec) {
			if(data[instance*OCTAT_SIZE+1] != 0) 
				updateInstance(ssbo, (int) data[instance*OCTAT_SIZE+1], true);
			if(data[instance*OCTAT_SIZE+2] != 0) 
				updateInstance(ssbo, (int) data[instance*OCTAT_SIZE+2], true);
			if(data[instance*OCTAT_SIZE+3] != 0) 
				updateInstance(ssbo, (int) data[instance*OCTAT_SIZE+3], true);
			if(data[instance*OCTAT_SIZE+4] != 0) 
				updateInstance(ssbo, (int) data[instance*OCTAT_SIZE+4], true);
			if(data[instance*OCTAT_SIZE+5] != 0) 
				updateInstance(ssbo, (int) data[instance*OCTAT_SIZE+5], true);
			if(data[instance*OCTAT_SIZE+6] != 0) 
				updateInstance(ssbo, (int) data[instance*OCTAT_SIZE+6], true);
			if(data[instance*OCTAT_SIZE+7] != 0) 
				updateInstance(ssbo, (int) data[instance*OCTAT_SIZE+7], true);
			if(data[instance*OCTAT_SIZE+8] != 0) 
				updateInstance(ssbo, (int) data[instance*OCTAT_SIZE+8], true);
		}
	}
	
	public float[] getData() {
		return data;
	}
	public boolean[] getSet() {
		return set;
	}

}
