package Kartoffel.Licht.Rendering.Shapes;

import Kartoffel.Licht.Geo.AABB;
import Kartoffel.Licht.Tools.Tools;

public class Shape extends ShapeData{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3100682714533350700L;
	public AABB bounds;
	
	public Shape main(float[] ver, float[] tex, int[] ind, float[] nor) {
		this.ver = ver;
		this.tex = tex;
		this.ind = ind;
		this.nor = nor;
		return this;
	}
	
	public Shape main(float[] ver, float[] tex, int[] ind, float[] nor, int[] mat, int[] boneID, float[] boneWt) {
			this.ver = ver;
			this.tex = tex;
			this.ind = ind;
			this.nor = nor;
			this.mat = mat;
			this.BoneIds = boneID;
			this.BoneWeights = boneWt;
		return this;
	}

	public Shape main(ShapeData dat) {
		this.ver = dat.ver;
		this.tex = dat.tex;
		this.ind = dat.ind;
		this.nor = dat.nor;
		this.mat = dat.mat;
		return this;
	}
	
	public final Shape calcBoundingBox(int samples) {
		bounds = new AABB();
		bounds.x1 = Double.POSITIVE_INFINITY;
		bounds.y1 = Double.POSITIVE_INFINITY;
		bounds.z1 = Double.POSITIVE_INFINITY;
		bounds.x2 = Double.NEGATIVE_INFINITY;
		bounds.y2 = Double.NEGATIVE_INFINITY;
		bounds.z2 = Double.NEGATIVE_INFINITY;
		for(int i = 0; i < ver.length; i+=3*samples) {
			if(ver[i+0] < bounds.x1)
				bounds.x1 = ver[i+0];
			else if(ver[i+0] > bounds.x2)
				bounds.x2 = ver[i+0];
			else if(ver[i+1] < bounds.y1)
				bounds.y1 = ver[i+1];
			else if(ver[i+1] > bounds.y2)
				bounds.y2 = ver[i+1];
			else if(ver[i+2] < bounds.z1)
				bounds.z1 = ver[i+2];
			else if(ver[i+2] > bounds.z2)
				bounds.z2 = ver[i+2];
		}
		return this;
	}
	/**
	 * Applies the Shape to this. only works for Triangulated Shapes<br>
	 * a shape will be corrupted, when...<br>
	 * ...there is excessive data stored in the shape<br>
	 * ...there is data missing<br>
	 * <br>
	 *
	 * @param shape
	 * @return this
	 */
	public final Shape add(ShapeData shape) {
		
		
		//Corruption check
		if(shape.ver.length/3 != shape.nor.length/3 || shape.ver.length/3 != shape.tex.length/2) {
			Tools.err("Failed;; Ver: " + shape.ver.length/3 + " Norm: " + shape.nor.length/3 + " Tex: " + shape.tex.length/2);
		}
		//Indices
		if(shape.ind != null) {
			if(this.ind == null)
				this.ind = new int[0];
			int[] ind = new int[shape.ind.length+this.ind.length];
			for(int i = 0; i < this.ind.length; i++) {
				ind[i] = this.ind[i];
			}
			int vl = 0;
			if(this.ver != null)
				vl = this.ver.length;
			for(int i = 0; i < shape.ind.length; i++) {
				ind[this.ind.length+i] = shape.ind[i]+vl/3;
			}
			this.ind = ind;
		}
		//Vertices
		if(shape.ver != null) {
			if(this.ver == null)
				this.ver = new float[0];
			float[] vert = new float[shape.ver.length+this.ver.length];
			for(int i = 0; i < this.ver.length; i++) {
				vert[i] = this.ver[i];
			}
			for(int i = 0; i < shape.ver.length; i++) {
				vert[this.ver.length+i] = shape.ver[i];
			}
			this.ver = vert;
		}
		//Normals
		if(shape.nor != null) {
			if(this.nor == null)
				this.nor = new float[0];
			float[] nor = new float[shape.nor.length+this.nor.length];
			for(int i = 0; i < this.nor.length; i++) {
				nor[i] = this.nor[i];
			}
			for(int i = 0; i < shape.nor.length; i++) {
				nor[this.nor.length+i] = shape.nor[i];
			}
			this.nor = nor;
		}
		//Texture coords
		if(shape.tex != null) {
			if(this.tex == null)
				this.tex = new float[0];
			float[] tex = new float[shape.tex.length+this.tex.length];
			for(int i = 0; i < this.tex.length; i++) {
				tex[i] = this.tex[i];
			}
			for(int i = 0; i < shape.tex.length; i++) {
				tex[this.tex.length+i] = shape.tex[i];
			}
			this.tex = tex;
		}
		//Materials
		if(shape.mat != null) {
			if(this.mat == null)
				this.mat = new int[0];
			int[] mat = new int[shape.mat.length+this.mat.length];
			for(int i = 0; i < this.mat.length; i++) {
				mat[i] = this.mat[i];
			}
			for(int i = 0; i < shape.mat.length; i++) {
				mat[this.mat.length+i] = shape.mat[i];
			}
			this.mat = mat;
		}
		//Animation
		if(shape.BoneIds != null) {
			if(this.BoneIds == null)
				this.BoneIds = new int[0];
			int[] bon = new int[shape.BoneIds.length+this.BoneIds.length];
			for(int i = 0; i < this.BoneIds.length; i++) {
				bon[i] = this.BoneIds[i];
			}
			for(int i = 0; i < shape.BoneIds.length; i++) {
				bon[this.BoneIds.length+i] = shape.BoneIds[i];
			}
			this.BoneIds = bon;
		}
		if(shape.BoneWeights != null) {
			if(this.BoneWeights == null)
				this.BoneWeights = new float[0];
			float[] wei = new float[shape.BoneWeights.length+this.BoneWeights.length];
			for(int i = 0; i < this.BoneWeights.length; i++) {
				wei[i] = this.BoneWeights[i];
			}
			for(int i = 0; i < shape.BoneWeights.length; i++) {
				wei[this.BoneWeights.length+i] = shape.BoneWeights[i];
			}
			this.BoneWeights = wei;
		}
		return this;
	}
	
	public final ShapeData set(ShapeData shape) {
		System.arraycopy(shape.ver, 0, this.ver=new float[this.ver.length], 0, shape.ver.length);
		System.arraycopy(shape.tex, 0, this.tex=new float[this.tex.length], 0, shape.tex.length);
		System.arraycopy(shape.ind, 0, this.ind=new int[this.ind.length], 0, shape.ind.length);
		System.arraycopy(shape.nor, 0, this.nor=new float[this.nor.length], 0, shape.nor.length);
		System.arraycopy(shape.mat, 0, this.mat=new int[this.mat.length], 0, shape.mat.length);
		return this;
	}
	
	public final ShapeData get(ShapeData shape) {
		System.arraycopy(this.ver, 0, shape.ver=new float[this.ver.length], 0, this.ver.length);
		System.arraycopy(this.tex, 0, shape.tex=new float[this.tex.length], 0, this.tex.length);
		System.arraycopy(this.ind, 0, shape.ind=new int[this.ind.length], 0, this.ind.length);
		System.arraycopy(this.nor, 0, shape.nor=new float[this.nor.length], 0, this.nor.length);
		System.arraycopy(this.mat, 0, shape.mat=new int[this.mat.length], 0, this.mat.length);
		return shape;
	}

	public AABB getBounds() {
		return bounds;
	}

	public Shape setBounds(AABB bounds) {
		this.bounds = bounds;
		return this;
	}
	
	
}

