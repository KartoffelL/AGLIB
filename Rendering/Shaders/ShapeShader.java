package Kartoffel.Licht.Rendering.Shaders;

import java.util.ArrayList;
import java.util.List;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import Kartoffel.Licht.Engine.ShadowMapper;
import Kartoffel.Licht.Java.ModifiableVariable;
import Kartoffel.Licht.Rendering.Camera;
import Kartoffel.Licht.Rendering.GEntity;
import Kartoffel.Licht.Rendering.Shaders.Objects.Light;
import Kartoffel.Licht.Rendering.Texture.FBOTexture;
import Kartoffel.Licht.Res.FileLoader;
import Kartoffel.Licht.Tools.Timer;




//Object that can render.
public class ShapeShader extends Shader{
	
	public static float AMBIENT_MULTIPL = 0.1f;
	
	private Camera camera;
	private FBOTexture depth;
	private ShadowMapper mapper;
	
	private Vector3f color = new Vector3f(.5f);
	private Vector3f colorMul = new Vector3f(1);
	private float stepSize1 = .01f;
	private int maxSamples1 = 128;
	private float increase1 = .1f;
	private float increase2 = .5f;
	private int colorSampleRate = 8;
	private float seedInfluence = 0.1f;
	private float density = .01f;
	private boolean infinite = false;
	private boolean effect = false;
	
	@ModifiableVariable
	private Vector3f GlobalLightDirection = new Vector3f(0, -1, 0);
	@ModifiableVariable
	private Vector3f GlobalLightColor = new Vector3f(1.6f, 1.6f, 1.6f);
	@ModifiableVariable
	private List<Light> Lights = new ArrayList<Light>();

	public ShapeShader(Camera cam, FBOTexture depth, ShadowMapper mapper) {
		super(FileLoader.readFileD("default/shaders/shape.vert"), FileLoader.readFileD("default/shaders/shape.frag"), null, "Shape Shader (Sphere Traced)");
		camera = cam;
		this.depth = depth;
		this.mapper = mapper;
	}
	
	private Matrix4f mat = new Matrix4f();
	@Override
	public void draw(GEntity gentity) {
		if(gentity == null)
			return;
		this.bind();
		this.setUniformMatrix4f("projectionMat",camera.getProjection());
		this.setUniformMatrix4f("viewMat",camera.getViewMatrix());
		this.setUniformMatrix4f("viewMatInv",camera.getViewMatrixInv());
		this.setUniformMatrix4f("transformationMat",gentity.getTransformationMatrix());
		this.setUniformMatrix4f("transformationMatInv",gentity.getTransformationMatrix().scale(1/gentity.getScale().x, 1/gentity.getScale().y, 1/gentity.getScale().z, mat).invert());
		mapper.setUniforms(this);
		this.setUniformMatrix4f("pvm", camera.getProViewMatrix());
		this.setUniformVec3("size", gentity.getScale());
		
		this.setUniformInt("albedo", 2);
		this.setUniformInt("depth", 0);
		this.setUniformInt("shadow", 1);
		
		this.setUniformVec3("DirectionalLight.direction", GlobalLightDirection);
		this.setUniformVec3("DirectionalLight.color", GlobalLightColor);
		this.setUniformVec3("DirectionalLight.ambient", GlobalLightColor.x*AMBIENT_MULTIPL, GlobalLightColor.y*AMBIENT_MULTIPL, GlobalLightColor.z*AMBIENT_MULTIPL);
		
		this.setUniformFloat("time", Timer.getTimeSecondsF());
		this.setUniformVec3("color", color);
		this.setUniformVec3("colorMul", colorMul);
		this.setUniformFloat("stepSize1", stepSize1);
		this.setUniformInt("maxSamples", maxSamples1);
		this.setUniformFloat("increase1", increase1);
		this.setUniformFloat("increase2", increase2);
		this.setUniformFloat("colorSampleRate", colorSampleRate);
		this.setUniformFloat("seedInfluence", seedInfluence);
		this.setUniformFloat("density", density);
		this.setUniformBool("infinite", infinite);
		this.setUniformBool("effect", effect);
		
		
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
		
		if(gentity.getTex() != null) {
			gentity.getTex().bind(2);
		}
		depth.bind(0);
		mapper.getTexture().bind(1);
		if(gentity.getMod() != null)
			gentity.getMod().render();
		this.bindNullShader();
	}
	public void setCamera(Camera camera) {
		this.camera = camera;
	}
	public Camera getCamera() {
		return camera;
	}
	
	public Light addLight(Vector3f position, Vector3f color) {
		Light l = new Light(position, color);
		Lights.add(l);
		return l;
				
	}
	
	public Light addSpotLight(Vector3f position, Vector3f color, Vector3f direction, float cutoff, float edge) {
		Light l = new Light(position, color, direction, edge, cutoff);
		Lights.add(l);
		return l;
	}
	
	public void removeLight(int l) {
		Lights.remove(l);
	}
	public void clearLights() {
		Lights.clear();
	}


	public Vector3f getGlobalLightDirection() {
		return GlobalLightDirection;
	}


	public void setGlobalLightDirection(Vector3f globalLightDirection) {
		GlobalLightDirection = globalLightDirection;
	}
	public void setGlobalLightDirection(float a, float b, float c) {
		GlobalLightDirection.x = a;
		GlobalLightDirection.y = b;
		GlobalLightDirection.z = c;
	}


	public Vector3f getGlobalLightColor() {
		return GlobalLightColor;
	}


	public void setGlobalLightColor(Vector3f globalLightColor) {
		GlobalLightColor = globalLightColor;
	}
	public void setGlobalLightColor(float a, float b, float c) {
		GlobalLightColor.x = a;
		GlobalLightColor.y = b;
		GlobalLightColor.z = c;
	}
	
	public void setLights(List<Light> lights) {
		Lights = lights;
	}
	public List<Light> getLights() {
		return Lights;
	}
	public FBOTexture getDepth() {
		return depth;
	}
	public void setDepth(FBOTexture depth) {
		this.depth = depth;
	}
	public ShadowMapper getMapper() {
		return mapper;
	}
	public void setMapper(ShadowMapper mapper) {
		this.mapper = mapper;
	}
	public Vector3f getColor() {
		return color;
	}
	public void setColor(Vector3f color) {
		this.color = color;
	}
	public Vector3f getColorMul() {
		return colorMul;
	}
	public void setColorMul(Vector3f colorMul) {
		this.colorMul = colorMul;
	}
	public float getStepSize1() {
		return stepSize1;
	}
	public void setStepSize1(float stepSize1) {
		this.stepSize1 = stepSize1;
	}
	public int getMaxSamples1() {
		return maxSamples1;
	}
	public void setMaxSamples1(int maxSamples1) {
		this.maxSamples1 = maxSamples1;
	}
	public float getIncrease1() {
		return increase1;
	}
	public void setIncrease1(float increase1) {
		this.increase1 = increase1;
	}
	public float getIncrease2() {
		return increase2;
	}
	public void setIncrease2(float increase2) {
		this.increase2 = increase2;
	}
	public float getDensity() {
		return density;
	}
	public void setDensity(float density) {
		this.density = density;
	}
	public boolean isInfinite() {
		return infinite;
	}
	public void setInfinite(boolean infinite) {
		this.infinite = infinite;
	}
	public boolean isEffect() {
		return effect;
	}
	public void setEffect(boolean effect) {
		this.effect = effect;
	}
	public Matrix4f getMat() {
		return mat;
	}
	public void setMat(Matrix4f mat) {
		this.mat = mat;
	}
	public void setSeedInfluence(float seedInfluence) {
		this.seedInfluence = seedInfluence;
	}
	public float getSeedInfluence() {
		return seedInfluence;
	}
	public void setColorSampleRate(int colorSampleRate) {
		this.colorSampleRate = colorSampleRate;
	}
	public int getColorSampleRate() {
		return colorSampleRate;
	}
	


}