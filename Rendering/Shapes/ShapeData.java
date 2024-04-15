package Kartoffel.Licht.Rendering.Shapes;

import java.io.Serializable;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import Kartoffel.Licht.Geo.translate;

public class ShapeData implements Serializable{
	private static final long serialVersionUID = 7867712420025666318L;
	public int matl;
	public int drawType = -1;
	public int fillMode = -1;
	
	public float[] ver;
	public int[] ind;
	
	//Optional
	public float[] tex;
	public float[] nor;
	public int[] mat;
	public int[] BoneIds;
	public float[] BoneWeights;
	
	public ShapeData move(float xoff, float yoff, float zoff) {
		for(int i = 0; i < ver.length; i += 3) {
			ver[i+0] += xoff;
			ver[i+1] += yoff;
			ver[i+2] += zoff;
		}
		return this;
	}
	
	public ShapeData scale(float xs, float ys, float zs) {
		for(int i = 0; i < ver.length; i += 3) {
			ver[i+0] *= xs;
			ver[i+1] *= ys;
			ver[i+2] *= zs;
		}
		return this;
	}
	
	public ShapeData scale(float s) {
		for(int i = 0; i < ver.length; i += 3) {
			ver[i+0] *= s;
			ver[i+1] *= s;
			ver[i+2] *= s;
		}
		return this;
	}
	
	public ShapeData rotate(float rx, float ry, float rz) {
		Matrix4f mat = new Matrix4f();
		mat.identity();
		mat.rotateXYZ((float)Math.toRadians(rx), (float)Math.toRadians(ry), (float)Math.toRadians(rz));
		apply(mat);
		return this;
	}
	
	public ShapeData rotateRad(float rx, float ry, float rz) {
		Matrix4f mat = new Matrix4f();
		mat.identity();
		mat.rotateXYZ(rx, ry, rz);
		apply(mat);
		return this;
	}
	
	public ShapeData smoothTriangles() {
		if(ind.length/3.0 != (int)(ind.length/3.0))
			throw new RuntimeException("Primitive type of this model isin�t Triangles! " + ind.length + "/3 =" + (ind.length/3.0));
		for(int i = 0; i < ind.length; i += 3) {
			float x1 = ver[ind[i+0]*3+0];
			float y1 = ver[ind[i+0]*3+1];
			float z1 = ver[ind[i+0]*3+2];
			
			float x2 = ver[ind[i+1]*3+0];
			float y2 = ver[ind[i+1]*3+1];
			float z2 = ver[ind[i+1]*3+2];
			
			float x3 = ver[ind[i+2]*3+0];
			float y3 = ver[ind[i+2]*3+1];
			float z3 = ver[ind[i+2]*3+2];
			
			float ux = x2-x1;
			float uy = y2-y1;
			float uz = z2-z1;
			
			float vx = x3-x1;
			float vy = y3-y1;
			float vz = z3-z1;
			
			float nx = uy*vz-uz*vy;
			float ny = uz*vx-ux*vz;
			float nz = ux*vy-uy*vx;
			
		    float scalar = org.joml.Math.invsqrt(Math.fma(nx, nx, Math.fma(ny, ny, nz * nz)));
		    nx = nx * scalar;
		    ny = ny * scalar;
		    nz = nz * scalar;
		    
			
			nor[ind[i+0]*3+0] = nx;
			nor[ind[i+0]*3+1] = ny;
			nor[ind[i+0]*3+2] = nz;
			
			nor[ind[i+1]*3+0] = nx;
			nor[ind[i+1]*3+1] = ny;
			nor[ind[i+1]*3+2] = nz;
			
			nor[ind[i+2]*3+0] = nx;
			nor[ind[i+2]*3+1] = ny;
			nor[ind[i+2]*3+2] = nz;
		}
		return this;
	}
	
	public ShapeData center() {
		float cx = 0, cy = 0, cz = 0;
		for(int i = 0; i < ver.length; i += 3) {
			cx += ver[i+0];
			cy += ver[i+1];
			cz += ver[i+2];
		}
		cx /= ver.length;
		cy /= ver.length;
		cz /= ver.length;
		move(-cx, -cy, -cz);
		return this;
	}
	
	public ShapeData apply(translate t) {
		for(int i = 0; i < ver.length; i += 3) {
			Vector3f d = t.m(ver[i+0], ver[i+1], ver[i+2], i);
			ver[i+0] = d.x;
			ver[i+1] = d.y;
			ver[i+2] = d.z;
		}
		return this;
	}
	
	public ShapeData apply(Matrix4f mat) {
		for(int i = 0; i < ver.length; i += 3) {
			Vector4f v = new Vector4f(ver[i+0], ver[i+1], ver[i+2], 0);
			v.mul(mat);
			ver[i+0] = v.x;
			ver[i+1] = v.y;
			ver[i+2] = v.z;
		}
		return this;
	}
	
	public ShapeData apply(translate t, int in, int out) {
		//1= ver, 2 = tex, 3 = nor, 4 = mat
		for(int i = 0; i < ver.length/3; i++) {
			Vector3f v = null;
			switch (in) {
				case 1:
					v = t.m(ver[i*3+0], ver[i*3+1], ver[i*3+2], i);
					break;
				case 2:
					v = t.m(tex[i*2+0], tex[i*2+1], 1, i);
					break;
				case 3:
					v = t.m(nor[i*3+0], nor[i*3+1], nor[i*3+2], i);
					break;
				case 4:
					v = t.m(mat[i+0], 1, 1, i);
					break;
			}
			switch (out) {
				case 1:
					ver[i*3+0] = v.x;
					ver[i*3+1] = v.y;
					ver[i*3+2] = v.z;
					break;
				case 2:
					tex[i*2+0] = v.x;
					tex[i*2+1] = v.y;
					break;
				case 3:
					nor[i*3+0] = v.x;
					nor[i*3+1] = v.y;
					nor[i*3+2] = v.z;
					break;
				case 4:
					mat[i+0] = (int) v.x;
					break;
			}
		}
		return this;
	}
	
	
	public ShapeData invertNormals() {
		for(int i = 0; i < nor.length; i += 3) {
			nor[i+0] *= -1;
			nor[i+1] *= -1;
			nor[i+2] *= -1;
		}
		return this;
	}
	
	public ShapeData transformTexture(int x, int y, int total_x, int total_y) {
		for(int i = 0; i < tex.length; i += 2) {
			tex[i+0] /= total_x;
			tex[i+0] += (float)x/total_x;
			tex[i+1] /= total_y;
			tex[i+1] += (float)y/total_y;
		}
		return this;
	}
	public ShapeData localizeTexture(int axis) {
		for(int i = 0; i < ver.length/3; i += 1) {
			if(axis == 0) {
				tex[i*2+0] = ver[i*3+0];
				tex[i*2+1] = ver[i*3+1];
			}
			else if(axis == 1) {
				tex[i*2+0] = ver[i*3+1];
				tex[i*2+1] = ver[i*3+2];
			}
			else if(axis == 2) {
				tex[i*2+0] = ver[i*3+0];
				tex[i*2+1] = ver[i*3+2];
			}
		}
		return this;
	}
	
	public ShapeData setNormals(float x, float y, float z) {
		for(int i = 0; i < nor.length; i += 3) {
			nor[i+0] = x;
			nor[i+1] = y;
			nor[i+2] = z;
		}
		return this;
	}
	
	public ShapeData flipFaces() {
		for(int i = 0; i < ind.length; i += 3) {
			int a = ind[i+0];
			ind[i+0] = ind[i+1];
			ind[i+1] = a;
		}
		return this;
	}
	public ShapeData tesselate(int count) {
		for(int i = 0; i < count; i++)
			tesselate();
		return this;
	}
	public ShapeData tesselate() {
		float[] new_vert = new float[ind.length];
		float[] new_nor = new float[ind.length];
		float[] new_tex = new float[ind.length/3*2];
		int[] faces = new int[ind.length*3];
		
		//Per face
		for(int FACE = 0; FACE < ind.length; FACE+=3) {
			//Adding a extra Vert to every face
			float x1 = ver[ind[FACE+0]*3+0];
			float y1 = ver[ind[FACE+0]*3+1];
			float z1 = ver[ind[FACE+0]*3+2];
			
			float x2 = ver[ind[FACE+1]*3+0];
			float y2 = ver[ind[FACE+1]*3+1];
			float z2 = ver[ind[FACE+1]*3+2];
			
			float x3 = ver[ind[FACE+2]*3+0];
			float y3 = ver[ind[FACE+2]*3+1];
			float z3 = ver[ind[FACE+2]*3+2];
			
			float x4 = (x1+x2+x3)/3;
			float y4 = (y1+y2+y3)/3;
			float z4 = (z1+z2+z3)/3;
			new_vert[FACE+0] = x4;
			new_vert[FACE+1] = y4;
			new_vert[FACE+2] = z4;
			
			//Adding a extra Nor to every face
			float nx1 = nor[ind[FACE+0]*3+0];
			float ny1 = nor[ind[FACE+0]*3+1];
			float nz1 = nor[ind[FACE+0]*3+2];
			
			float nx2 = nor[ind[FACE+1]*3+0];
			float ny2 = nor[ind[FACE+1]*3+1];
			float nz2 = nor[ind[FACE+1]*3+2];
			
			float nx3 = nor[ind[FACE+2]*3+0];
			float ny3 = nor[ind[FACE+2]*3+1];
			float nz3 = nor[ind[FACE+2]*3+2];
			
			float nx4 = (nx1+nx2+nx3)/3;
			float ny4 = (ny1+ny2+ny3)/3;
			float nz4 = (nz1+nz2+nz3)/3;
			new_nor[FACE+0] = nx4;
			new_nor[FACE+1] = ny4;
			new_nor[FACE+2] = nz4;
			
			//Adding a extra Tex to every face
			float tx1 = tex[ind[FACE+0]*2+0];
			float ty1 = tex[ind[FACE+0]*2+1];
			
			float tx2 = tex[ind[FACE+1]*2+0];
			float ty2 = tex[ind[FACE+1]*2+1];
			
			float tx3 = tex[ind[FACE+2]*2+0];
			float ty3 = tex[ind[FACE+2]*2+1];
			
			float tx4 = (tx1+tx2+tx3)/3;
			float ty4 = (ty1+ty2+ty3)/3;
			new_tex[FACE/3*2+0] = tx4;
			new_tex[FACE/3*2+1] = ty4;
			
			//Setting the faces
			
			//Triangle 1
			faces[FACE*3+0] = ind[FACE+0];
			faces[FACE*3+1] = ind[FACE+1];
			faces[FACE*3+2] = (ver.length+FACE)/3;
			//Triangle 2
			faces[FACE*3+3] = ind[FACE+0];
			faces[FACE*3+4] = (ver.length+FACE)/3;
			faces[FACE*3+5] = ind[FACE+2];
			//Triangle 3
			faces[FACE*3+6] = (ver.length+FACE)/3;
			faces[FACE*3+7] = ind[FACE+1];
			faces[FACE*3+8] = ind[FACE+2];
			
		}
		//System.out.println("Tesselated " + ind.length/3 + " faces. now " + faces.length/3);
		this.ind = faces;
		float[] vert = new float[new_vert.length+ver.length];
		System.arraycopy(ver, 0, vert, 0, ver.length);
		System.arraycopy(new_vert, 0, vert, ver.length, new_vert.length);
		this.ver = vert;
		
		float[] norr = new float[new_nor.length+nor.length];
		System.arraycopy(nor, 0, norr, 0, nor.length);
		System.arraycopy(new_nor, 0, norr, nor.length, new_nor.length);
		this.nor = norr;
		
		float[] texx = new float[new_tex.length+tex.length];
		System.arraycopy(tex, 0, texx, 0, tex.length);
		System.arraycopy(new_tex, 0, texx, tex.length, new_tex.length);
		this.tex = texx;
		
		return this;
	}

	public int getMatl() {
		return matl;
	}

	public ShapeData setMatl(int matl) {
		this.matl = matl;
		return this;
	}

	public int getDrawType() {
		return drawType;
	}

	public ShapeData setDrawType(int drawType) {
		this.drawType = drawType;
		return this;
	}

	public float[] getVer() {
		return ver;
	}

	public ShapeData setVer(float[] ver) {
		this.ver = ver;
		return this;
	}

	public int[] getInd() {
		return ind;
	}

	public ShapeData setInd(int[] ind) {
		this.ind = ind;
		return this;
	}

	public float[] getTex() {
		return tex;
	}

	public ShapeData setTex(float[] tex) {
		this.tex = tex;
		return this;
	}

	public float[] getNor() {
		return nor;
	}

	public ShapeData setNor(float[] nor) {
		this.nor = nor;
		return this;
	}

	public int[] getMat() {
		return mat;
	}

	public ShapeData setMat(int[] mat) {
		this.mat = mat;
		return this;
	}

	public int[] getBoneIds() {
		return BoneIds;
	}

	public ShapeData setBoneIds(int[] boneIds) {
		BoneIds = boneIds;
		return this;
	}

	public float[] getBoneWeights() {
		return BoneWeights;
	}

	public ShapeData setBoneWeights(float[] boneWeights) {
		BoneWeights = boneWeights;
		return this;
	}
	
	public void setFillMode(int fillMode) {
		this.fillMode = fillMode;
	}
	
	public int getFillMode() {
		return fillMode;
	}
	
	@Override
	public String toString() {
		return "[V: " + ver.length + ", I:"+ind.length+", M: " + matl + "]";
	}
	
}
