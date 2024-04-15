package Kartoffel.Licht.Rendering.Texture.Material;

import org.joml.Vector3f;

public class Material implements MaterialType{
	private static final long serialVersionUID = 2003522042712668624L;

	public String name = "Material";
	
	//ambient+diffuse+specular = 1,												A	  D	 Ss	S   E
	public static final Material MATERIAL_DEFAULT = new Material(new float[] {0.2f, 1f, 0.03125f, 0.4f, 0f, 0.1f});
	public static final Material MATERIAL_ROUGHT = new Material(new float[] {0.2f, 0.8f, 0.0f, 0.0f, 0f, 0});
	public static final Material MATERIAL_FLAT = new Material(new float[] {0.5f, 0.5f, 0F, 0f, 0f, 0.2f});
	public static final Material MATERIAL_SHINY = new Material(new float[] {0.2f, 0.0f, 0.5f, 0.5f, 0f, 0.5f});
	public static final Material MATERIAL_EMMISIVE = new Material(new float[] {1.0f, 0.0f, 0.0f, 0.0f, 1f, 0});
	
	
	Vector3f ambient;	//Ka
	Vector3f diffuse;	//Kd
	
	Vector3f specular;	//Ks
	float specularS;	//Ns
	Vector3f emmisiveC;
	float emmisiveI;
	
	float metallicI;
	
	int illum = 2;

	public Material(float[] data) {
		if(data.length > 5) {
			ambient = new Vector3f(data[0]);
			diffuse = new Vector3f(data[1]);
			specularS = data[2];
			specular = new Vector3f(data[3]);
			emmisiveI = data[4];
			emmisiveC = new Vector3f(data[4]);
			metallicI = data[5];
		}
	}
	public Material() {
			ambient = new Vector3f(1);
			diffuse = new Vector3f(0f, 1f, 1f);
			specularS = 0;
			specular = new Vector3f(0);
			emmisiveC = new Vector3f(0);
			emmisiveI = 0;
	}

	public float getAmbient() {
		return ambient.x;
	}
	public Vector3f getAmbientC() {
		return ambient;
	}

	public void setAmbient(float ambient) {
		this.ambient = new Vector3f(ambient);
	}

	public float getDiffuse() {
		return diffuse.x;
	}
	public Vector3f getDiffuseC() {
		return diffuse;
	}

	public void setDiffuse(float diffuse) {
		this.diffuse = new Vector3f(ambient);
	}

	public float getSpecularS() {
		return specularS;
	}

	public void setSpecularS(float specularS) {
		this.specularS = specularS;
	}

	public float getSpecular() {
		return specular.x;
	}
	public Vector3f getSpecularC() {
		return specular;
	}

	public void setSpecular(float specular) {
		this.specular = new Vector3f(specular);
	}
	public void setAmbient(Vector3f ambient) {
		this.ambient = ambient;
	}
	public void setDiffuse(Vector3f diffuse) {
		this.diffuse = diffuse;
	}
	public void setSpecular(Vector3f specular) {
		this.specular = specular;
	}
	
	public Vector3f getEmmisiveC() {
		return emmisiveC;
	}
	
	public float getEmmisiveI() {
		return emmisiveI;
	}
	public void setEmmisiveC(Vector3f emmisiveC) {
		this.emmisiveC = emmisiveC;
	}
	public void setEmmisiveI(float emmisiveI) {
		this.emmisiveI = emmisiveI;
	}
	
	public void setIllum(int illum) {
		this.illum = illum;
	}
	public int getIllum() {
		return illum;
	}
	
	public float getMetallicI() {
		return metallicI;
	}
	
	public void setMetallicI(float metallicI) {
		this.metallicI = metallicI;
	}
	
	@Override
	public String toString() {
		return "Material: A:" + ambient + " D:" + diffuse + " S:" + specular + " E:" + emmisiveC;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}
	

}
