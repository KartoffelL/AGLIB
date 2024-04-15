package Kartoffel.Licht.Rendering.Shaders;

import java.util.ArrayList;
import java.util.List;

import org.joml.Random;
import org.joml.Vector2f;
import org.joml.Vector3f;

import Kartoffel.Licht.Geo.SparseVoxelOcttree;
import Kartoffel.Licht.Java.BufferedImage;
import Kartoffel.Licht.Java.Color;
import Kartoffel.Licht.Java.ModifiableVariable;
import Kartoffel.Licht.Rendering.Camera;
import Kartoffel.Licht.Rendering.GEntity;
import Kartoffel.Licht.Rendering.Model;
import Kartoffel.Licht.Rendering.Shaders.Objects.Light;
import Kartoffel.Licht.Rendering.Shaders.Objects.ShaderStorage;
import Kartoffel.Licht.Rendering.Shapes.SBox2D;
import Kartoffel.Licht.Rendering.Texture.Renderable;
import Kartoffel.Licht.Rendering.Texture.Texture;
import Kartoffel.Licht.Rendering.Texture.Material.PBRMaterial;
import Kartoffel.Licht.Res.FileLoader;
import Kartoffel.Licht.Tools.Tools;




//Object that can render.
public class VoxelShader extends Shader{

	public static float AMBIENT_MULTIPL = 0.1f;
	
	Model m;
	Texture tex;
	Camera camera;
	
	private final PBRMaterial[] mat = new PBRMaterial[256];
	private int MAX_TESTS = 512;
	private int MAX_BOUNCES = 64;
	
	@ModifiableVariable
	private Vector3f GlobalLightDirection = new Vector3f(0, -1, 0);
	@ModifiableVariable
	private Vector3f GlobalLightColor = new Vector3f(1.6f, 1.6f, 1.6f);
	@ModifiableVariable
	private List<Light> Lights = new ArrayList<Light>();
	
	public VoxelShader(Camera camera) {
		super(FileLoader.readFileD("default/shaders/rshaderV.vert"), FileLoader.readFileD("default/shaders/rshaderV.frag"), "R SHADER");
		this.main(camera, 128);
	}
	private void main(Camera camera, int randomTextureSize) {
		m = new Model(SBox2D.XM().scale(2));
		this.camera = camera;
		this.bind();
		Random r = new Random();
		BufferedImage bi = new BufferedImage(randomTextureSize, randomTextureSize, 1);
		for(int i = 0; i < bi.getWidth(); i++) {
			for(int l = 0; l < bi.getHeight(); l++) {
				Vector3f v = new Vector3f((float)rvnd(r), (float)rvnd(r), (float)rvnd(r));
				//Vector3f v = new Vector3f(r.nextFloat()*2-1, r.nextFloat()*2-1, r.nextFloat()*2-1);
				v.normalize(0.5f);
				bi.setRGBA(l, i, new Color(v.x+.5f, v.y+.5f, v.z+.5f).getRGBA());
			}
		}
		tex = new Texture(bi);
		this.setUniformBlockBinding("grid", 0);
		mat[0] = new PBRMaterial(0, 0, 0, 0, 0, 0, 0, 1.000293f);
	}

	private double rvnd(Random r) {
		double theta = 2 * 3.1415926 * r.nextFloat();
		double rho = Math.sqrt(-2 * Math.log(r.nextFloat()));
		return rho * Math.cos(theta);
	}
	
	@Override
	protected void setUniforms(GEntity entity) {
		this.setUniformMatrix4f("projectionMat",camera.getProjection());
		this.setUniformMatrix4f("viewMat",camera.getViewMatrix());
		this.setUniformMatrix4f("viewMatInv",camera.getViewMatrixInv());
		this.setUniformVec2("random", Tools.RANDOM.nextFloat(), Tools.RANDOM.nextFloat());
		this.setUniformVec2("clip", new Vector2f(camera.getProjectionBox().getZ_NEAR(), camera.getProjectionBox().getZ_FAR()));
		this.setUniformInt("sampler0", 0);
		this.setUniformInt("MAX_DEPTH", SparseVoxelOcttree.MAX_TREE_DEPTH);
		this.setUniformInt("MAX_TESTS", MAX_TESTS);
		this.setUniformInt("MAX_BOUNCES", MAX_BOUNCES);
		
		this.setUniformVec3("DirectionalLight.direction", GlobalLightDirection);
		this.setUniformVec3("DirectionalLight.color", GlobalLightColor);
		this.setUniformVec3("DirectionalLight.ambient", GlobalLightColor.x*AMBIENT_MULTIPL, GlobalLightColor.y*AMBIENT_MULTIPL, GlobalLightColor.z*AMBIENT_MULTIPL);
		//Lights
		int asl = 0;
		int apl = 0;
		for(int i = 0;i < Lights.size(); i++) {
			boolean spot = Lights.get(i).spotlight;
			Lights.get(i).addLight(this, spot ? asl : apl);
			if(spot)
				asl++;
			else
				apl++;
		}
		this.setUniformInt("plAm", apl);
		this.setUniformInt("slAm", asl);
	}
	public void updateMaterials() {
		this.bind();
		for(int i = 0; i < this.mat.length; i++) {
			if(mat[i] != null) {
				this.setUniformVec4("materials["+i+"].color", mat[i].getAlbedo());
				this.setUniformFloat("materials["+i+"].emissive", mat[i].getEmissive());
				this.setUniformFloat("materials["+i+"].roughtness", mat[i].getRoughness());
				this.setUniformFloat("materials["+i+"].metallic", mat[i].getMetallic());
				this.setUniformFloat("materials["+i+"].refraction", mat[i].getRefraction());
			}
		}
	}
	@Override
	protected void draw(GEntity gentity) {
		throw new RuntimeException("Cant render Entities with this Shader!");
	}
	
	@Override
	protected void draw(Model mod, Renderable tex) {
		throw new RuntimeException("Cant render Entities with this Shader!");
	}
	
	public void draw(ShaderStorage data, float xoff, float yoff, float zoff, float size) {
		this.bind();
		this.setUniforms(null);
		this.setUniformVec4("offset", xoff, yoff, zoff, size);
		data.bindUniformBlock(0);
		tex.bind(0);
		m.render();
		this.bindNullShader();
	}
	
	@Override
	public void free() {
		super.free();
		tex.free();
		m.free();
	}
	public Camera getCamera() {
		return camera;
	}
	
	public int getMAX_TESTS() {
		return MAX_TESTS;
	}
	public void setMAX_TESTS(int mAX_TESTS) {
		MAX_TESTS = mAX_TESTS;
	}
	public int getMAX_BOUNCES() {
		return MAX_BOUNCES;
	}
	public void setMAX_BOUNCES(int mAX_BOUNCES) {
		MAX_BOUNCES = mAX_BOUNCES;
	}
	public PBRMaterial[] getMaterials() {
		return mat;
	}
	public List<Light> getLights() {
		return Lights;
	}
	public void setLights(List<Light> lights) {
		Lights = lights;
	}
	public Vector3f getGlobalLightColor() {
		return GlobalLightColor;
	}
	public void setGlobalLightColor(Vector3f globalLightColor) {
		GlobalLightColor = globalLightColor;
	}
	public Vector3f getGlobalLightDirection() {
		return GlobalLightDirection;
	}
	public void setGlobalLightDirection(Vector3f globalLightDirection) {
		GlobalLightDirection = globalLightDirection;
	}
}