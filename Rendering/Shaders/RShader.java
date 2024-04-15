package Kartoffel.Licht.Rendering.Shaders;

import java.util.List;

import org.joml.Random;
import org.joml.Vector2f;
import org.joml.Vector3f;

import Kartoffel.Licht.Geo.AABB;
import Kartoffel.Licht.Geo.DSphere;
import Kartoffel.Licht.Geo.DefinableShape;
import Kartoffel.Licht.Java.BufferedImage;
import Kartoffel.Licht.Java.Color;
import Kartoffel.Licht.Rendering.Camera;
import Kartoffel.Licht.Rendering.GEntity;
import Kartoffel.Licht.Rendering.Model;
import Kartoffel.Licht.Rendering.Shapes.SBox2D;
import Kartoffel.Licht.Rendering.Texture.Renderable;
import Kartoffel.Licht.Rendering.Texture.Texture;
import Kartoffel.Licht.Res.FileLoader;
import Kartoffel.Licht.Tools.Tools;

public class RShader extends Shader{
	
	final public static int SPHERE_OBJECT_SIZE = 6; //4+1+1
	final public static int BOX_OBJECT_SIZE = 9; //4+4+1
	final public static int MATERIAL_SIZE = 11; //4+4+1+1+1
	
	
	Model m;
	Texture tex;
	Camera camera;
	private int MAX_BOUNCES = 4, SAMPLING = -1;
	private float WEIGHT = 1;
	private Vector3f ambient = new Vector3f(0);
	private boolean useSampler = false;
//	private static UniformBuffer ubo;

	public RShader(Camera camera) {
		super(FileLoader.readFileD("default/shaders/rshader.vert"), FileLoader.readFileD("default/shaders/rshader.frag"), "R SHADER");
		this.main(camera, 128);
	}
	public RShader(Camera camera, int randomTextureSize) {
		super(FileLoader.readFileD("default/shaders/rshader.vert"), FileLoader.readFileD("default/shaders/rshader.frag"), "R SHADER");
		this.main(camera, randomTextureSize);
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
//		if(ubo == null) {
//			float[] data = new float[MAX_OBJECT_PER_TYPE*SPHERE_OBJECT_SIZE+MAX_OBJECT_PER_TYPE*BOX_OBJECT_SIZE+256*MATERIAL_SIZE]; //256 Max materials
//			for(int i = 0; i < data.length; i++)
//				data[i] = 1;
//			ubo = new UniformBuffer(data);
//			ubo.bindUniformBlock(0);
//		}
		this.setUniformBlockBinding("objects", 0);
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
		this.setUniformInt("MAX_BOUNCES", MAX_BOUNCES);
		this.setUniformInt("SAMPLES", SAMPLING);
		this.setUniformFloat("WEIGHT", WEIGHT);
		this.setUniformVec2("clip", new Vector2f(camera.getProjectionBox().getZ_NEAR(), camera.getProjectionBox().getZ_FAR()));
		this.setUniformVec3("ambient", ambient);
		this.setUniformInt("sampler0", 0);
		this.setUniformBool("useSampler", useSampler);
	}
	
	public static void uniforms(Shader s, List<GEntity> shapes) {
		int am_sphere = 0;
		int am_box = 0;
		s.bind();
		for(GEntity g : shapes) {
			if(g == null)
				continue;
			DefinableShape sh = g.getShape();
			if(sh != null) {
				if(sh instanceof DSphere) {
					uniform_sphere(am_sphere++, g.getPosition(), g.getScale().x, 0, s);
				}
				if(sh instanceof AABB) {
					uniform_box(am_box++, g.getPosition(), g.getScale(), 0, s);
				}
			}
		}
//		for(int i = 0; i < am_box+am_sphere; i++) {
			uniform_material(0, new Vector3f(0.8f, 0.8f, 0.8f), new Vector3f(0), 0.5f, 0.5f, 1f, s);
//		}
		s.bind();
		s.setUniformInt("amount_spheres", am_sphere);
		s.setUniformInt("amount_boxes", am_box);
	}
	
	public void Template(Shader s) {
		s.bind();
		uniform_sphere(1, new Vector3f(0, -1001, -10), 1000, 0, s);
		uniform_sphere(0, new Vector3f(0, 1, -7), 0.5f, 1, s);
		uniform_box(0, new Vector3f(0.01f, 1, -4), new Vector3f(1, 1, 0.1f), 2, s);
		uniform_box(1, new Vector3f(2, 1, -4), new Vector3f(0.05f, 1, 4), 3, s);
		uniform_box(2, new Vector3f(0, 2, -6), new Vector3f(0.5f, 0.05f, 5f), 4, s);
		
		
		uniform_material(0, new Vector3f(0.7f), new Vector3f(0), 1, 1, 0, s);
		uniform_material(1, new Vector3f(0.867f, 0.518f, 0.9f), new Vector3f(0), 1.0f, 0.0f, 1/1.00f, s);
		uniform_material(2, new Vector3f(1, 1, 1), new Vector3f(0), 0, 1, 0, s);
		uniform_material(3, new Vector3f(1, 0, 0), new Vector3f(0), 1.0f, 0.0f, 1/1.05f, s);
		uniform_material(4, new Vector3f(0, 0, 0), new Vector3f(0.7f, 0.9f, 1).mul(5), 1.0f, 0.0f, 1/1.05f, s);
		
		uniform_amount(2, 3, s);
	}
	
	public void Template2(Shader s) {
		s.bind();
		uniform_sphere(0, new Vector3f(0, 1, -7), 0.5f, 1, s);
		uniform_box(0, new Vector3f(0.01f, 1, -4), new Vector3f(1, 1, 0.1f), 2, s);
		uniform_box(1, new Vector3f(2, 1, -4), new Vector3f(0.05f, 1, 0.6f), 3, s);
		uniform_box(2, new Vector3f(0, 2, -6), new Vector3f(0.5f, 0.05f, 0.5f), 4, s);
		
		
		uniform_material(0, new Vector3f(0.7f), new Vector3f(0), 0.9f, 1, 0, s);
		uniform_material(1, new Vector3f(0.867f, 0.518f, 0.9f), new Vector3f(0), 0.9f, 1, 0, s);
		uniform_material(2, new Vector3f(1, 1, 1), new Vector3f(0), 0.9f, 1, 0, s);
		uniform_material(3, new Vector3f(1, 0, 0), new Vector3f(0), 0.9f, 1, 0, s);
		uniform_material(4, new Vector3f(0, 0, 0), new Vector3f(0.7f, 0.9f, 1).mul(5), 1, 1, 0, s);
		
		uniform_amount(1, 3, s);
	}
	
	public void Template3(Shader s) {
		s.bind();
		
		uniform_box(0, new Vector3f(0, -1, 0), new Vector3f(1, 0, 1), 0, s);
		uniform_box(1, new Vector3f(0, 1, 0), new Vector3f(1, 0, 1), 1, s);
		uniform_box(2, new Vector3f(-1, 0, 0), new Vector3f(0, 1, 1), 2, s);
		uniform_box(3, new Vector3f(1, 0, 0), new Vector3f(0, 1, 1), 3, s);
		uniform_box(4, new Vector3f(0, 0, -1), new Vector3f(1, 1, 0), 4, s);
		uniform_box(5, new Vector3f(0, 0, 1), new Vector3f(1, 1, 0), 5, s);
		
		uniform_box(6, new Vector3f(0, 1.1f, 0), new Vector3f(0.7f, 0.05f, 0.7f), 6, s);
		
		uniform_sphere(0, new Vector3f(0.8f, -0.7f, -0.3f), 0.4f, 7, s);
		uniform_sphere(1, new Vector3f(-0.8f, -0.7f,-0.3f), 0.4f, 8, s);
		uniform_sphere(2, new Vector3f(0,0.3f,0.3f), 0.3f, 9, s);
		
		uniform_material(0, new Vector3f(1, 1, 1), new Vector3f(0), 0, 1, 0, s);
		uniform_material(1, new Vector3f(1, 1, 1), new Vector3f(0), 1, 1, 0, s);
		uniform_material(2, new Vector3f(1, 0.1f, 0.1f), new Vector3f(0), 1, 1, 0, s);
		uniform_material(3, new Vector3f(0.1f, 0.1f, 1), new Vector3f(0), 1, 1, 0, s);
		uniform_material(4, new Vector3f(0.7f, 1, 0.7f), new Vector3f(0), 0.8f, 1, 0, s);
		uniform_material(5, new Vector3f(0.7f, 1, 0.7f), new Vector3f(0), 1, 1, 0, s);
		
		uniform_material(6, new Vector3f(1), new Vector3f(12), 1, 1, 0, s);
		
		uniform_material(7, new Vector3f(1, 0.1f, 0.5f), new Vector3f(0), 0.8f, 1, 0, s);
		
		uniform_material(8, new Vector3f(0.5f, 0.1f, 1f), new Vector3f(0), 0.1f, 1, 0, s);
		
		uniform_material(9, new Vector3f(1), new Vector3f(0), 0.1f, 0, 0.8f, s);
		
		uniform_amount(3, 7, s);
	}
	
	public void uniform_amount(int spheres, int boxes, Shader s) {
		s.setUniformInt("amount_spheres", spheres);
		s.setUniformInt("amount_boxes", boxes);
	}
	
	public static void uniform_box(int index, Vector3f pos, Vector3f size, int material, Shader s) {
		s.setUniformVec3("boxes["+index+"].pos", pos);
		s.setUniformVec3("boxes["+index+"].size", size);
		s.setUniformInt("boxes["+index+"].mat", material);
//		int offset = MAX_OBJECT_PER_TYPE*5;
//		ubo.getData()[offset+index*7+0] = pos.x;
//		ubo.getData()[offset+index*7+1] = pos.y;
//		ubo.getData()[offset+index*7+2] = pos.z;
//		ubo.getData()[offset+index*7+3] = size.x;
//		ubo.getData()[offset+index*7+4] = size.y;
//		ubo.getData()[offset+index*7+5] = size.z;
//		ubo.getData()[offset+index*7+6] = material;
//		ubo.updateData(offset+index*7, offset+index*7+6);
	}
	
	public static void uniform_box(int index, float[] data, Shader s) {
		s.setUniformVec3("boxes["+index+"].pos", new Vector3f(data[0], data[1], data[2]));
		s.setUniformVec3("boxes["+index+"].size", new Vector3f(data[3], data[4], data[5]));
		s.setUniformInt("boxes["+index+"].mat", (int)data[6]);
		
//		int offset = MAX_OBJECT_PER_TYPE*5;
//		ubo.getData()[offset+index*7+0] = data[0];
//		ubo.getData()[offset+index*7+1] = data[1];
//		ubo.getData()[offset+index*7+2] = data[2];
//		ubo.getData()[offset+index*7+3] = data[3];
//		ubo.getData()[offset+index*7+4] = data[4];
//		ubo.getData()[offset+index*7+5] = data[5];
//		ubo.getData()[offset+index*7+6] = data[6];
//		ubo.updateData(offset+index*7, offset+index*7+6);
	}
	
	public static void uniform_sphere(int index, Vector3f pos, float radius, int material, Shader s) {
		s.setUniformVec3("spheres["+index+"].pos", pos);
		s.setUniformFloat("spheres["+index+"].radius", radius);
		s.setUniformInt("spheres["+index+"].mat", material);
//		ubo.getData()[index*SPHERE_OBJECT_SIZE+0] = pos.x;
//		ubo.getData()[index*SPHERE_OBJECT_SIZE+1] = pos.y;
//		ubo.getData()[index*SPHERE_OBJECT_SIZE+2] = pos.z;
//		ubo.getData()[index*SPHERE_OBJECT_SIZE+4] = radius;
//		ubo.getData()[index*SPHERE_OBJECT_SIZE+5] = material;
//		ubo.updateData(index*SPHERE_OBJECT_SIZE, index*SPHERE_OBJECT_SIZE+SPHERE_OBJECT_SIZE);
//		System.out.println(index*SPHERE_OBJECT_SIZE + " to " + (index*SPHERE_OBJECT_SIZE+SPHERE_OBJECT_SIZE));
	}
	
	public static void uniform_sphere(int index, float[] data, Shader s) {
		s.setUniformVec3("spheres["+index+"].pos", new Vector3f(data[0], data[1], data[2]));
		s.setUniformFloat("spheres["+index+"].radius", data[3]);
		s.setUniformInt("spheres["+index+"].mat", (int)data[4]);
//		ubo.getData()[index*5+0] = data[0];
//		ubo.getData()[index*5+1] = data[1];
//		ubo.getData()[index*5+2] = data[2];
//		ubo.getData()[index*5+3] = data[3];
//		ubo.getData()[index*5+4] = data[4];
//		ubo.updateData(index*5, index*5+4);
		System.err.println("NOT UPDATED");
	}
	
	public static void uniform_material(int index, Vector3f color, Vector3f e_color, float r, float alpha, float refraction, Shader s) {
		s.setUniformVec3("materials["+index+"].color", color);
		s.setUniformVec3("materials["+index+"].e_color", e_color);
		s.setUniformFloat("materials["+index+"].roughness", r);
		s.setUniformFloat("materials["+index+"].alpha", alpha);
		s.setUniformFloat("materials["+index+"].refraction", refraction);
//		int offset = MAX_OBJECT_PER_TYPE*5+MAX_OBJECT_PER_TYPE*7;
//		ubo.getData()[offset+index*9+0] = color.x;
//		ubo.getData()[offset+index*9+1] = color.y;
//		ubo.getData()[offset+index*9+2] = color.z;
//		ubo.getData()[offset+index*9+3] = e_color.x;
//		ubo.getData()[offset+index*9+4] = e_color.y;
//		ubo.getData()[offset+index*9+5] = e_color.z;
//		ubo.getData()[offset+index*9+6] = r;
//		ubo.getData()[offset+index*9+7] = alpha;
//		ubo.getData()[offset+index*9+8] = refraction;
//		ubo.updateData(offset+index*9, offset+index*9+8);
	}
	
	public static void uniform_material(int index, float[] data, Shader s) {
		s.setUniformVec3("materials["+index+"].color", new Vector3f(data[0], data[1], data[2]));
		s.setUniformVec3("materials["+index+"].e_color", new Vector3f(data[3], data[4], data[5]));
		s.setUniformFloat("materials["+index+"].roughness", data[6]);
		s.setUniformFloat("materials["+index+"].alpha", data[7]);
		s.setUniformFloat("materials["+index+"].refraction", data[8]);
//		int offset = MAX_OBJECT_PER_TYPE*5+MAX_OBJECT_PER_TYPE*9;
//		ubo.getData()[offset+index*9+0] = data[0];
//		ubo.getData()[offset+index*9+1] = data[1];
//		ubo.getData()[offset+index*9+2] = data[2];
//		ubo.getData()[offset+index*9+3] = data[3];
//		ubo.getData()[offset+index*9+4] = data[4];
//		ubo.getData()[offset+index*9+5] = data[5];
//		ubo.getData()[offset+index*9+6] = data[6];
//		ubo.getData()[offset+index*9+7] = data[7];
//		ubo.getData()[offset+index*9+8] = data[8];
//		ubo.updateData(offset+index*9, offset+index*9+8);
	}
	
	@Override
	protected void draw(GEntity gentity) {
		throw new RuntimeException("Cant render Entities with this Shader!");
	}
	
	@Override
	protected void draw(Model mod, Renderable tex) {
		throw new RuntimeException("Cant render Entities with this Shader!");
	}
	
	public void draw() {
		this.bind();
		this.setUniforms(null);
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
	
	public void setMAX_BOUNCES(int mAX_BOUNCES) {
		MAX_BOUNCES = mAX_BOUNCES;
	}
	
	public void setSAMPLES(int samples) {
		SAMPLING = samples;
	}
	
	public void setWEIGHT(float wEIGHT) {
		WEIGHT = wEIGHT;
	}
	public void setAmbient(Vector3f ambient) {
		this.ambient = ambient;
	}
	public int getSAMPLES() {
		return SAMPLING;
	}
	public float getWEIGHT() {
		return WEIGHT;
	}
	public int getMAX_BOUNCES() {
		return MAX_BOUNCES;
	}
	public Vector3f getAmbient() {
		return ambient;
	}
	
	public void setUseSampler(boolean useSampler) {
		this.useSampler = useSampler;
	}
	public boolean isUseSampler() {
		return useSampler;
	}
	public Camera getCamera() {
		return camera;
	}
}
