package Kartoffel.Licht.Rendering.Shapes;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector3f;

import Kartoffel.Licht.Rendering.Shapes.CubicArrayMeshC.CubeDictionary;
import Kartoffel.Licht.Rendering.Shapes.CubicArrayMeshC.CubicArrayMeshData;

public class CubicArrayMesh extends Shape{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2091190745930712291L;
	private static final float dis = 0.5f;
	private static final float siz = 0.5f;
	private static final float tzm = 0.00f;
	/**
	 * Breaks Textures. Can be used for faster rendering and less memory usage.
	 * 
	 * 
	 */
	private static final boolean MemoryEff = false;

	public CubicArrayMesh(CubeDictionary dir, CubicArrayMeshData data) {
		super.main(generateDat(dir, data));
	}
	
	
	//private HashMap<Vector3f, Integer> virt = new HashMap<Vector3f, Integer>();
	
	private ShapeData generateDat(CubeDictionary dir, CubicArrayMeshData data) {
		ShapeData sd = new ShapeData();
		List<Float> vert = new ArrayList<Float>();
		List<Float> tex = new ArrayList<Float>();
		List<Float> nor = new ArrayList<Float>();
		List<Integer> ind = new ArrayList<Integer>();
		
		for(int i = 0; i < data.SizeX; i++) {
			for(int l = 0; l < data.SizeY; l++) {
				for(int k = 0; k < data.SizeZ; k++) {
					if(data.getCube(i, l, k) != 0) {//#################################
						int te = dir.get(data.getCube(i, l, k)).textureID;
						if(dir.get(data.getCube(i-1, l, k)).transparent)
							addFace(i-dis, l-siz, k+siz,
									i-dis, l+siz, k+siz,
									i-dis, l-siz, k-siz,
									i-dis, l+siz, k-siz, vert, tex, ind, nor, dir, te, -1f, 0f, 0f, false);
						
						if(dir.get(data.getCube(i+1, l, k)).transparent)
							addFace(i+dis, l-siz, k+siz,
									i+dis, l+siz, k+siz,
									i+dis, l-siz, k-siz,
									i+dis, l+siz, k-siz, vert, tex, ind, nor, dir, te, +1f, 0f, 0f, true);
						
						if(dir.get(data.getCube(i, l-1, k)).transparent)
							addFace(i-siz, l-dis, k+siz,
									i+siz, l-dis, k+siz,
									i-siz, l-dis, k-siz,
									i+siz, l-dis, k-siz, vert, tex, ind, nor, dir, te, 0f, -1f, 0f, true);
						
						if(dir.get(data.getCube(i, l+1, k)).transparent)
							addFace(i-siz, l+dis, k+siz,
									i+siz, l+dis, k+siz,
									i-siz, l+dis, k-siz,
									i+siz, l+dis, k-siz, vert, tex, ind, nor, dir, te, 0f, +1f, 0f, false);
						
						if(dir.get(data.getCube(i, l, k-1)).transparent)
							addFace(i-siz, l+siz, k-dis,
									i+siz, l+siz, k-dis,
									i-siz, l-siz, k-dis,
									i+siz, l-siz, k-dis, vert, tex, ind, nor, dir, te, 0f, 0f, -1f, false);
						
						if(dir.get(data.getCube(i, l, k+1)).transparent)
							addFace(i-siz, l+siz, k+dis,
									i+siz, l+siz, k+dis,
									i-siz, l-siz, k+dis,
									i+siz, l-siz, k+dis, vert, tex, ind, nor, dir, te, 0f, 0f, +1f, true);
					}//################################################################
				}
			}
		}
		float[] res = new float[vert.size()];
		for(int i = 0; i < vert.size(); i++)
			res[i] = vert.get(i);
		sd.ver = res;
		
		float[] res2 = new float[tex.size()];
		for(int i = 0; i < tex.size(); i++)
			res2[i] = tex.get(i);
		sd.tex = res2;
		
		int[] resi = new int[ind.size()];
		for(int i = 0; i < ind.size(); i++)
			resi[i] = ind.get(i);
		sd.ind = resi;
		
		float[] res3 = new float[nor.size()];
		for(int i = 0; i < nor.size(); i++)
			res3[i] = nor.get(i);
		sd.nor = res3;
		
		return sd;
	}
	
	
	private int num = 0;
	
	public void addFace(float a1, float a2, float a3, float b1, float b2, float b3, float c1, float c2, float c3, float d1, float d2, float d3, List<Float> vert, List<Float> texture, List<Integer> ind, List<Float> norm, CubeDictionary dir, int tex, float i, float l, float k, boolean n) {
		if(MemoryEff)
			addFaceS(a1, a2, a3, b1, b2, b3, c1, c2, c3, d1, d2, d3, vert, texture, ind, norm, dir, tex, i, l, k, n);	//Smoooth
		else
			addFaceG(a1, a2, a3, b1, b2, b3, c1, c2, c3, d1, d2, d3, vert, texture, ind, norm, dir, tex, i, l, k, n);	//Good lookin�
	}
	
	public void addFaceG(float a1, float a2, float a3, float b1, float b2, float b3, float c1, float c2, float c3, float d1, float d2, float d3, List<Float> vert, List<Float> texture, List<Integer> ind, List<Float> norm, CubeDictionary dir, int tex, float i, float l, float k, boolean n) {
		if(!n) {//Normals by GL
			ind.add(num+0); //Dreieck 1
			ind.add(num+1);
			ind.add(num+2);
			ind.add(num+2); //Dreieck 2
			ind.add(num+1);
			ind.add(num+3);
			
			vert.add(a1);						//   #   O
			vert.add(a2);						//   O   O
			vert.add(a3);
			num++;
			vert.add(b1);						//   O   #
			vert.add(b2);						//   O   O
			vert.add(b3);
			num++;
			vert.add(c1);						//   O   O
			vert.add(c2);						//   #   O
			vert.add(c3);
			num++;
			vert.add(d1);						//   O   O
			vert.add(d2);						//   O   #
			vert.add(d3);
			num++;
			
		}else {
			ind.add(num+0); //Dreieck 1
			ind.add(num+2);
			ind.add(num+1);
			ind.add(num+1); //Dreieck 2
			ind.add(num+2);
			ind.add(num+3);
					   							//		   1
			vert.add(a1);						//   #   O
			vert.add(a2);						//   O   O
			vert.add(a3);
			num++;								//		   1
			vert.add(b1);						//   O   #
			vert.add(b2);						//   O   O
			vert.add(b3);
			num++;								//		   1
			vert.add(c1);						//   O   O
			vert.add(c2);						//   #   O
			vert.add(c3);
			num++;								//		   1
			vert.add(d1);						//   O   O
			vert.add(d2);						//   0   #
			vert.add(d3);
			num++;
			
		}
		
		norm.add(i);
		norm.add(l);
		norm.add(k);
		
		norm.add(i);
		norm.add(l);
		norm.add(k);
		
		norm.add(i);
		norm.add(l);
		norm.add(k);
		
		norm.add(i);
		norm.add(l);
		norm.add(k);
		
		float tileSize = 1.0f/dir.TCS;
		
		texture.add((float) ((tex % (double)dir.TCS)  )*tileSize+tzm);
		texture.add((float) (tileSize*(Math.floor(tex*(1.0/dir.TCS))+1  )-tzm));
		
		texture.add((float) ((tex % (double)dir.TCS)+1  )*tileSize-tzm);
		texture.add((float) (tileSize*(Math.floor(tex*(1.0/dir.TCS))+1  )-tzm));
		
		texture.add((float) ((tex % (double)dir.TCS)  )*tileSize+tzm);
		texture.add((float) (tileSize*(Math.floor(tex*(1.0/dir.TCS))  )+tzm));
		
		texture.add((float) ((tex % (double)dir.TCS)+1  )*tileSize-tzm);
		texture.add((float) (tileSize*(Math.floor(tex*(1.0/dir.TCS))  )+tzm));
	}
	
	public void addFaceS(float a1, float a2, float a3, float b1, float b2, float b3, float c1, float c2, float c3, float d1, float d2, float d3, List<Float> vert, List<Float> texture, List<Integer> ind, List<Float> norm, CubeDictionary dir, int tex, float i, float l, float k, boolean n) {
		int ind1, ind2, ind3, ind4;
		float tileSize = 1.0f/dir.TCS;
		if(!n) {//Normals by GL
			ind1 = containsFloats(vert, a1, a2, a3);
			if(ind1 == -1) {
				ind1 = num;
				vert.add(a1);						//   #   O
				vert.add(a2);						//   O   O
				vert.add(a3);
				norm.add(i);
				norm.add(l);
				norm.add(k);
				texture.add((float) ((tex % (double)dir.TCS)  )*tileSize);
				texture.add((float) (tileSize*(Math.floor(tex*(1.0/dir.TCS))+1  )));
				num++;
			}
			ind2 = containsFloats(vert, b1, b2, b3);
			if(ind2 == -1) {
				ind2 = num;
				vert.add(b1);						//   O   #
				vert.add(b2);						//   O   O
				vert.add(b3);
				norm.add(i);
				norm.add(l);
				norm.add(k);
				texture.add((float) ((tex % (double)dir.TCS)+1  )*tileSize);
				texture.add((float) (tileSize*(Math.floor(tex*(1.0/dir.TCS))+1  )));
				num++;
			}
			ind3 = containsFloats(vert, c1, c2, c3);
			if(ind3 == -1) {
				ind3 = num;
				vert.add(c1);						//   O   O
				vert.add(c2);						//   #   O
				vert.add(c3);
				norm.add(i);
				norm.add(l);
				norm.add(k);
				texture.add((float) ((tex % (double)dir.TCS)  )*tileSize);
				texture.add((float) (tileSize*(Math.floor(tex*(1.0/dir.TCS))  )));
				num++;
			}
			ind4 = containsFloats(vert, d1, d2, d3);
			if(ind4 == -1) {
				ind4 = num;
				vert.add(d1);						//   O   O
				vert.add(d2);						//   O   #
				vert.add(d3);
				norm.add(i);
				norm.add(l);
				norm.add(k);
				texture.add((float) ((tex % (double)dir.TCS)+1  )*tileSize);
				texture.add((float) (tileSize*(Math.floor(tex*(1.0/dir.TCS))  )));
				num++;
			}
			
			ind.add(ind1); //Dreieck 1
			ind.add(ind2);
			ind.add(ind3);
			ind.add(ind3); //Dreieck 2
			ind.add(ind2);
			ind.add(ind4);
			
		}else {
			ind1 = containsFloats(vert, a1, a2, a3);
			if(ind1 == -1) {
				ind1 = num;
				vert.add(a1);						//   #   O
				vert.add(a2);						//   O   O
				vert.add(a3);
				norm.add(i);
				norm.add(l);
				norm.add(k);
				texture.add((float) ((tex % (double)dir.TCS)  )*tileSize);
				texture.add((float) (tileSize*(Math.floor(tex*(1.0/dir.TCS))+1  )));
				num++;
			}
			ind2 = containsFloats(vert, b1, b2, b3);
			if(ind2 == -1) {
				ind2 = num;
				vert.add(b1);						//   O   #
				vert.add(b2);						//   O   O
				vert.add(b3);
				norm.add(i);
				norm.add(l);
				norm.add(k);
				texture.add((float) ((tex % (double)dir.TCS)+1  )*tileSize);
				texture.add((float) (tileSize*(Math.floor(tex*(1.0/dir.TCS))+1  )));
				num++;
			}
			ind3 = containsFloats(vert, c1, c2, c3);
			if(ind3 == -1) {
				ind3 = num;
				vert.add(c1);						//   O   O
				vert.add(c2);						//   #   O
				vert.add(c3);
				norm.add(i);
				norm.add(l);
				norm.add(k);
				texture.add((float) ((tex % (double)dir.TCS)  )*tileSize);
				texture.add((float) (tileSize*(Math.floor(tex*(1.0/dir.TCS))  )));
				num++;
			}
			ind4 = containsFloats(vert, d1, d2, d3);
			if(ind4 == -1) {
				ind4 = num;
				vert.add(d1);						//   O   O
				vert.add(d2);						//   O   #
				vert.add(d3);
				norm.add(i);
				norm.add(l);
				norm.add(k);
				texture.add((float) ((tex % (double)dir.TCS)+1  )*tileSize);
				texture.add((float) (tileSize*(Math.floor(tex*(1.0/dir.TCS))  )));
				num++;
			}
			
			ind.add(ind1); //Dreieck 1
			ind.add(ind3);
			ind.add(ind2);
			ind.add(ind2); //Dreieck 2
			ind.add(ind3);
			ind.add(ind4);
			
		}
		
	}
	
	public void addAll(List<Float> f, Vector3f v) {
		f.add(v.x);
		f.add(v.y);
		f.add(v.z);
	}
	
	private int containsFloats(List<Float> list, float a, float b, float c) {
		int index = -1;
		for(int i = 0; i < list.size(); i+= 3) {
			if(list.get(i+0) == a)
				if(list.get(i+1) == b)
					if(list.get(i+2) == c) {
						index = i/3;
						break;
					}
		}
		return index;
		
	}
	
	
	

}
