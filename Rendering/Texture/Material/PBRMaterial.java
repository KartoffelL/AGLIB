package Kartoffel.Licht.Rendering.Texture.Material;

import org.joml.Vector4f;

import Kartoffel.Licht.Java.Color;

public class PBRMaterial implements MaterialType{
	private static final long serialVersionUID = -1416224090103356373L;
	//ambient+diffuse+specular = 1,												A	  D	 Ss	S   E
	public static final PBRMaterial MATERIAL_DEFAULT = new PBRMaterial(new float[] {0.8f, 1f, 0.0f, 0.5f, 0.05f});
	public static final PBRMaterial MATERIAL_ROUGHT = new PBRMaterial(new float[] {0.8f, 1f, 0.000f, 0.2f, 0});
	public static final PBRMaterial MATERIAL_FLAT = new PBRMaterial(new float[] {0.8f, 0.5f, 0F, 0f, 0.05f});
	public static final PBRMaterial MATERIAL_SHINY = new PBRMaterial(new float[] {0.8f, 0.0f, 0.5f, 0.5f, 0.05f});
	public static final PBRMaterial MATERIAL_EMMISIVE = new PBRMaterial(new float[] {1.0f, 0.0f, 0.0f, 0.0f, 1});
	
	
	private String name = "PBR Material";
	
	Vector4f albedo = new Vector4f();	//Ka
	float metallic;	//Ns
	float roughness;	//Ks
	float Ao;    //
	float emissive;
	float refraction;

	public PBRMaterial(float[] data) {
		if(data.length > 5) {
			albedo = new Vector4f(data[0]);
			metallic = data[1];
			roughness = data[2];
			Ao = data[3];
			emissive = data[4];
		}
	}
	public PBRMaterial(float r, float g, float b, float a, float metallic, float roughness, float emissive, float refraction) {
		this.albedo = new Vector4f(r, g, b, a);
		this.metallic = metallic;
		this.roughness = roughness;
		this.emissive = emissive;
		this.refraction = refraction;
	}
	public PBRMaterial() {
			albedo = new Vector4f(0);
			metallic = 0;
			roughness = 0;
			Ao = 0;
	}
	
	public Vector4f getAlbedo() {
		return albedo;
	}
	public PBRMaterial setAlbedo(Vector4f albedo) {
		this.albedo = albedo;
		return this;
	}
	public PBRMaterial setAlbedo(Color albedo) {
		this.albedo.x = albedo.getRed()/255f;
		this.albedo.y = albedo.getGreen()/255f;
		this.albedo.z = albedo.getBlue()/255f;
		this.albedo.w = albedo.getAlpha()/255f;
		return this;
	}
	public float getMetallic() {
		return metallic;
	}
	public PBRMaterial setMetallic(float metallic) {
		this.metallic = metallic;
		return this;
	}
	public float getRoughness() {
		return roughness;
	}
	public PBRMaterial setRoughness(float roughness) {
		this.roughness = roughness;
		return this;
	}
	public float getAo() {
		return Ao;
	}
	public PBRMaterial setAo(float ao) {
		Ao = ao;
		return this;
	}
	public float getEmissive() {
		return emissive;
	}
	public PBRMaterial setEmissive(float emissive) {
		this.emissive = emissive;
		return this;
	}
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}
	
	public float getRefraction() {
		return refraction;
	}
	
	public void setRefraction(float refraction) {
		this.refraction = refraction;
	}
	
	public PBRMaterial clone() throws CloneNotSupportedException {
		PBRMaterial m = new PBRMaterial();
		m.getAlbedo().set(albedo);
		m.setAo(Ao);
		m.setEmissive(emissive);
		m.setMetallic(metallic);
		m.setName(name);
		m.setRoughness(roughness);
		m.setRefraction(refraction);
		return m;
	}
	

}
