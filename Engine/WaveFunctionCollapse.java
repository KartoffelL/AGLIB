package Kartoffel.Licht.Engine;

import java.util.ArrayList;
import java.util.List;

import Kartoffel.Licht.Engine.WaveFunctionCollapse.dictionary.face;
import Kartoffel.Licht.Engine.WaveFunctionCollapse.dictionary.type;
import Kartoffel.Licht.Tools.Tools;

public class WaveFunctionCollapse {
	/**
	 * 
	 * Final look-up table for all provided building blocks 
	 */
	public static class dictionary {
		public type[] types;
		protected configurationTable table;
		public static class type {
			public short id;
			//Faces in local space of the type (cube)
			public final face[] faces;
			public type(int i, face xm, face xp, face ym, face yp, face zm, face zp) {
				this.id = (short) i;
				this.faces = new face[] {xm, xp, ym, yp, zm, zp};
			}
			@Override
			public String toString() {
				return "["+faces[0].toString()+","+faces[1].toString()+","+faces[2].toString()+","+faces[3].toString()+","+faces[4].toString()+","+faces[5].toString()+"]";
			}
		}
		public static class face {
			//General info of the face, global
			final material material;
			//Specific properties of the face
			final boolean mirr;
			final byte rot;
			public face(material material, boolean mirr, int rot) {
				this.mirr = mirr;
				this.rot = (byte) rot;
				this.material = material;
			}
			@Override
			public String toString() {
				return "["+material+","+material+"]";
			}
			
		}
		public static record material(short materialID, boolean symm) {
			public material(int i, boolean b) {
				this((short)i, b);
			}
			
		};
		public dictionary() {
			types = new type[4];
			final material SOLID = new material(0, true);
			final material AIR = new material(1, true);
			final material EDGE = new material(2, false);
			types[0] = new type(0, //Block
					new face(SOLID, false, 0),
					new face(SOLID, false, 0),
					new face(SOLID, false, 0),
					new face(SOLID, false, 0),
					new face(SOLID, false, 0),
					new face(SOLID, false, 0));
			
			types[1] = new type(1, //Strait
					new face(SOLID, false, 0),
					new face(AIR, false, 0),
					new face(SOLID, false, 0),
					new face(AIR, false, 0),
					new face(EDGE, false, 0),
					new face(EDGE, false, 0));
			
//			types[2] = new type(2, //Corner
//					new face(AIR, false, 0),
//					new face(EDGE, false, 0),
//					new face(SOLID, false, 0),
//					new face(AIR, false, 0),
//					new face(EDGE, false, 0),
//					new face(AIR, false, 0));
			
			types[3] = new type(3, //Air
					new face(AIR, false, 0),
					new face(AIR, false, 0),
					new face(AIR, false, 0),
					new face(AIR, false, 0),
					new face(AIR, false, 0),
					new face(AIR, false, 0));
		}
		@Override
		public String toString() {
			return "["+types.length+","+table.toString()+"]";
		}
	}
	/**
	 * Represents a cube
	 */
	public static class cube {
		public Kartoffel.Licht.Engine.WaveFunctionCollapse.configurationTable.configuration type;
		public cube(Kartoffel.Licht.Engine.WaveFunctionCollapse.configurationTable.configuration conf) {
			this.type =	conf;
		}
	}
	
	/**
	 * Final table containing all possible configurations of every block, including rotation and mirroring //512 shorts per block/ ~72KB for 72Blocks
	 */
	public static class configurationTable {
		
		public configuration[] configurations;
		
		public int createConfigFaceID(short materialID, boolean symm, int rot) {
			int res = materialID;
			if(!symm)
				res |= rot;
			return res;
		}
		
		protected void addEntry(List<configuration> configs, short id, int rotX, int rotY, int rotZ, boolean mirror, face[] faces) {
			int rot = 0;
			rot |= rotX;
			rot |= rotY<<2;
			rot |= rotZ<<4;
			rot = rot<<32-6;
			
			configs.add(new configuration(id, createState(rotX, rotY, rotZ, mirror, false, false),
					createConfigFaceID(faces[0].material.materialID, faces[0].material.symm, rot),
					createConfigFaceID(faces[1].material.materialID, faces[1].material.symm, rot),
					createConfigFaceID(faces[2].material.materialID, faces[2].material.symm, rot),
					createConfigFaceID(faces[3].material.materialID, faces[3].material.symm, rot),
					createConfigFaceID(faces[4].material.materialID, faces[4].material.symm, rot), 
					createConfigFaceID(faces[5].material.materialID, faces[5].material.symm, rot)));
		}
		
		public configurationTable(dictionary dic) {
			//Create configurations
			List<configuration> configs = new ArrayList<WaveFunctionCollapse.configurationTable.configuration>();
			//https://www.markronan.com/mathematics/symmetry-corner/the-rotations-of-a-cube/ -> A cube has 48 symmetries, 24 without mirroring
			for(int i = 0; i < dic.types.length; i++) {
				type t = dic.types[i];
				if(t == null)
					continue;
				//Without mirroring (24)
				addEntry(configs, t.id, 0, 0, 0, false, new face[] {t.faces[0], t.faces[1], t.faces[2], t.faces[3], t.faces[4], t.faces[5]});
				addEntry(configs, t.id, 0, 1, 0, false, new face[] {t.faces[5], t.faces[4], t.faces[2], t.faces[3], t.faces[1], t.faces[0]});
				addEntry(configs, t.id, 0, 2, 0, false, new face[] {t.faces[1], t.faces[0], t.faces[2], t.faces[3], t.faces[5], t.faces[4]});
				addEntry(configs, t.id, 0, 3, 0, false, new face[] {t.faces[4], t.faces[5], t.faces[2], t.faces[3], t.faces[0], t.faces[1]});
				
				//With mirroring (+24)
				
			}
			configurations = (configuration[]) configs.toArray(new configuration[configs.size()]);
		}
		
		public static class configuration {
			public short id;
			public int[] faceIDs;
			public short state;
			public configuration(short id, int state, int...faceIDs) {
				this.faceIDs = faceIDs;
				this.state = (short) state;
				this.id = id;
			}
			@Override
			public String toString() {
				return "["+id+","+state+"]";
			}
		}
		
		public short getEntropy(cube xm, cube xp, cube ym, cube yp, cube zm, cube zp) {
			return (short) getPossible(xm, xp, ym, yp, zm, zp).length;
		}
		
		public cube[] getPossible(cube xm, cube xp, cube ym, cube yp, cube zm, cube zp) {
			List<cube> cubes = new ArrayList<WaveFunctionCollapse.cube>();
			for(int t = 0; t < configurations.length; t++) {
				configuration type = configurations[t];
				boolean conf = true;
				if(xm != null) {
					conf = conf && type.faceIDs[XM] == xm.type.faceIDs[XP];
				}
				if(xp != null) {
					conf = conf && type.faceIDs[XP] == xp.type.faceIDs[XM];
				}
				if(ym != null) {
					conf = conf && type.faceIDs[YM] == ym.type.faceIDs[YP];
				}
				if(yp != null) {
					conf = conf && type.faceIDs[YP] == yp.type.faceIDs[YM];
				}
				if(zm != null) {
					conf = conf && type.faceIDs[ZM] == zm.type.faceIDs[ZP];
				}
				if(zp != null) {
					conf = conf && type.faceIDs[ZP] == zp.type.faceIDs[ZM];
				}
				if(conf)
					cubes.add(new cube(type));
				
			}
			return (cube[]) cubes.toArray(new cube[cubes.size()]);
		}

	}
	
	
	///------äääääääääääääääääääääääää------------------------ääääääääääääääääääääääääää-------------------------ääääääääääääääääääääää
	
	/**
	 * Max cube states, needs 9 bits to be stored
	 */
	public static final int MAX_CUBE_STATES = 512; //4*4*4*2*2*2 rotX*rotY*rotZ*mirrorX*mirrorY*mirrorZ
	
	public static final int MAX_FACE_STATES = 8; //4*2 rot*mirror
	
	public static short createState(int rotX, int rotY, int rotZ, boolean mirrorX, boolean mirrorY, boolean mirrorZ) {
		short result = 0;
		result |= rotX&0b0000000000000011;
		result |= (rotY&0b0000000000000011)<<2;
		result |= (rotZ&0b0000000000000011)<<4;
		if(mirrorX)
			result |= 0b0000000001000000;
		if(mirrorY)
			result |= 0b0000000010000000;
		if(mirrorZ)
			result |= 0b0000000100000000;
		return result;
	}
	public static class state {
		public byte rotX, rotY, rotZ;
		public boolean mirrorX, mirrorY, mirrorZ;
		@Override
		public String toString() {
			return "["+rotX+","+rotY+","+rotZ+";"+mirrorX+","+mirrorY+","+mirrorZ+"]";
		}
	}
	public static state getState(short s) {
		state state = new state();
		state.rotX = (byte) (s&0b0000000000000011);
		state.rotY = (byte) ((s>>2)&0b0000000000000011);
		state.rotZ = (byte) ((s>>4)&0b0000000000000011);
		state.mirrorX = (s&0b0000000001000000) != 0;
		state.mirrorY = (s&0b0000000010000000) != 0;
		state.mirrorZ = (s&0b0000000100000000) != 0;
		return state;
	}
	
	public final static int
			XM = 0,
			XP = 1,
			YM = 2,
			YP = 3,
			ZM = 4,
			ZP = 5;
	
	
	//Rotation table for rotating around axis clockwise
	private final static int[][] rotationTableX0 = new int[][] {{XM, XM, XM, XM},{XP, XP, XP, XP},{YM, ZP, YP, ZM},{YP, ZM, YM, ZP},{ZM, YM, ZP, YP},{ZP, YP, ZM, YM}};
	private final static int[][] rotationTableY0 = new int[][] {{XM, ZP, XP, ZM},{XP, ZM, XM, ZP},{YM, YM, YM, YM},{YP, YP, YP, YP},{ZM, XM, ZP, XP},{ZP, XP, ZM, XM}};
	private final static int[][] rotationTableZ0 = new int[][] {{XM, YP, XP, YM},{XP, YM, XM, YP},{YM, XM, YP, XP},{YP, XP, YM, XM},{ZM, ZM, ZM, ZM},{ZP, ZP, ZP, ZP}};
	
	//xm, xp, ym, yp, zm, zp
	public static int rotatePositionXYZ(int pos, int i, int j, int k) {
		int result = pos;
		result = rotationTableX0[result][i];
		result = rotationTableY0[result][j];
		result = rotationTableZ0[result][k];
		return result;
	}
	
	
	public static void calculateEntropy(cube[][][] prefered, short[][][] entropy, dictionary dict, int x, int y, int z) {
		//TODO Calculate entropy based on the original entropy, originating from the last affected cube
		final int[] xos = new int[] {-1, 1, 0, 0, 0, 0};
		final int[] yos = new int[] {0, 0, -1, 1, 0, 0};
		final int[] zos = new int[] {0 ,0, 0, 0, -1, 1};
		for(int i = 0; i < 6; i++) {
			int xo = xos[i];
			int yo = yos[i];
			int zo = zos[i];
			cube ids_xp = getSafe(prefered, x+xo+1, yo+y, zo+z);//Currently possible type of neighboring cubes
			cube ids_xm = getSafe(prefered, x+xo-1, yo+y, zo+z);
			cube ids_yp = getSafe(prefered, x+xo, yo+y+1, zo+z);
			cube ids_ym = getSafe(prefered, x+xo, yo+y-1, zo+z);
			cube ids_zp = getSafe(prefered, x+xo, yo+y, zo+z+1);
			cube ids_zm = getSafe(prefered, x+xo, yo+y, zo+z-1);
			setSafe(entropy, xo+x, yo+y, zo+z, dict.table.getEntropy(ids_xm, ids_xp, ids_ym, ids_yp, ids_zm, ids_zp));
			System.out.println(dict.table.getEntropy(ids_xm, ids_xp, ids_ym, ids_yp, ids_zm, ids_zp));
		}
	}
	
	public static void calculateEntropyNew(cube[][][] prefered, short[][][] entropy, dictionary dict) {
		//TODO Calculate entropy based on the original entropy, originating from the last affected cube
		for(int x = 0; x < entropy.length; x++) {
			for(int y = 0; y < entropy[0].length; y++) {
				for(int z = 0; z < entropy[0][0].length; z++) {
					cube ids_xp = getSafe(prefered, x+1, y, z);//Currently possible type of neighboring cubes
					cube ids_xm = getSafe(prefered, x-1, y, z);
					cube ids_yp = getSafe(prefered, x, y+1, z);
					cube ids_ym = getSafe(prefered, x, y-1, z);
					cube ids_zp = getSafe(prefered, x, y, z+1);
					cube ids_zm = getSafe(prefered, x, y, z-1);
					entropy[x][y][z] = dict.table.getEntropy(ids_xm, ids_xp, ids_ym, ids_yp, ids_zm, ids_zp);
				}
			}
		}
	}
	
	public static cube getSafe(cube[][][] entropy, int x, int y, int z) {
		return entropy[CubeArray3D.safe(x, entropy.length)][CubeArray3D.safe(y, entropy[0].length)][CubeArray3D.safe(z, entropy[0][0].length)];
	}
	public static void setSafe(boolean[][][] entropy, int x, int y, int z, boolean a) {
		entropy[CubeArray3D.safe(x, entropy.length)][CubeArray3D.safe(y, entropy[0].length)][CubeArray3D.safe(z, entropy[0][0].length)] = a;
	}
	public static void setSafe(short[][][] arr, int x, int y, int z, short entropy) {
		arr[CubeArray3D.safe(x, arr.length)][CubeArray3D.safe(y, arr[0].length)][CubeArray3D.safe(z, arr[0][0].length)] = entropy;
	}
	
	public static class triplet {
		public int x, y, z;
		public triplet(int x, int y, int z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}
		@Override
		public String toString() {
			return "["+x+","+y+","+z+"]";
		}
	}

	public static void fill(cube[][][] array, dictionary dic) {
		boolean[][][] baked = new boolean[array.length][array[0].length][array[0][0].length];
		short[][][] entropy = new short[array.length][array[0].length][array[0][0].length];
		
		//initially fill entropy with everything
		for(int x = 0; x < entropy.length; x++) {
			for(int y = 0; y < entropy[0].length; y++) {
				for(int z = 0; z < entropy[0][0].length; z++) {
					array[x][y][z] = null;
					entropy[x][y][z] = (short) dic.types.length; //All possible
				}
			}
		}
		dic.table = new configurationTable(dic);
		
		System.out.println("Created Config Table! Entries: " + dic.table.configurations.length);
		
		List<triplet> lowest = new ArrayList<WaveFunctionCollapse.triplet>();
		for(int i = 0; i < array.length*array[0].length*array[0][0].length; i++) {
			lowest.clear();
			short lowestVal = Short.MAX_VALUE;
			for(int x = 0; x < entropy.length; x++) {
				for(int y = 0; y < entropy[0].length; y++) {
					for(int z = 0; z < entropy[0][0].length; z++) {
						if(baked[x][y][z])
							continue;
						if(entropy[x][y][z] < lowestVal) {
							lowest.clear();
							lowestVal = entropy[x][y][z];
							lowest.add(new triplet(x, y, z));
						}
						else if(entropy[x][y][z] == lowestVal)
							lowest.add(new triplet(x, y, z));
					}
				}
			}
			triplet t = lowest.get(Tools.RANDOM.nextInt(lowest.size()));
			
			cube ids_xp = getSafe(array, t.x+1, t.y, t.z);//Currently possible type of neighboring cubes
			cube ids_xm = getSafe(array, t.x-1, t.y, t.z);
			cube ids_yp = getSafe(array, t.x, t.y+1, t.z);
			cube ids_ym = getSafe(array, t.x, t.y-1, t.z);
			cube ids_zp = getSafe(array, t.x, t.y, t.z+1);
			cube ids_zm = getSafe(array, t.x, t.y, t.z-1);
			cube[] poss = dic.table.getPossible(ids_xm, ids_xp, ids_ym, ids_yp, ids_zm, ids_zp);
			if(poss.length != 0) {
				array[t.x][t.y][t.z] = poss[Tools.RANDOM.nextInt(poss.length)];
				System.out.println("Collapsed " + t);
			}else {
				
			}
			baked[t.x][t.y][t.z] = true;
			calculateEntropy(array, entropy, dic, t.x, t.y, t.z);
		}
	}

}
