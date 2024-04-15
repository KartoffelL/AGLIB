package Kartoffel.Licht.Rendering.Shaders;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import Kartoffel.Licht.Rendering.Camera;
import Kartoffel.Licht.Rendering.GEntity;
import Kartoffel.Licht.Rendering.Model;
import Kartoffel.Licht.Rendering.Texture.Renderable;
import Kartoffel.Licht.Res.FileLoader;
import Kartoffel.Licht.Tools.Tools;




/**
 * Shaders are programs that can be uploaded to the GPU.
 * They can be bound if needed.
 * The Class auto-compiles everything.
 * If needed, Int, Float, Bool, Matrix4f and Vector3f can be set as an Uniform variable, accessible in the
 * Shader with the definition 
 * 'uniform', exmp.
 * 
 * uniform Vector3f color;
 * 
 * Names have to be exact and case-sensitive
 * 
 * 
 * @author PC
 *
 */
//Object that can render.
public class SkyboxShader extends Shader{
	
	private Camera camera;
	private boolean dynamic = true;
	private Vector3f up = new Vector3f(0, 1, 0);
	private Vector3f sun_dir = null;
	private Vector3f wavelenghts = new Vector3f(400f/700f, 400f/530f, 400f/440f);
	{
		wavelenghts.x = wavelenghts.x*wavelenghts.x*wavelenghts.x*wavelenghts.x;
		wavelenghts.y = wavelenghts.y*wavelenghts.y*wavelenghts.y*wavelenghts.y;
		wavelenghts.z = wavelenghts.z*wavelenghts.z*wavelenghts.z*wavelenghts.z;
	}
	private Vector3f sun = new Vector3f(1);
	private float tw = 512;
	private float ss = 4;
	private float sb = .1f;
	private float fl = 25;
	private Vector3f n_stars = new Vector3f(0.5f);
	private Vector3f n_color = new Vector3f(0, 0.004f/12, 0.122f/12);
	private Vector3f n_lines = new Vector3f(0.2f);
	private Vector3f n_moon = new Vector3f(0.6f);
	
	private boolean cool = false;
	
	public SkyboxShader(Camera cam) {
		super(FileLoader.readFileD("default/shaders/skybox.vert"), FileLoader.readFileD("default/shaders/skybox.frag"), "Skybox Shader");
		camera = cam;
	}
	
	@Override
	protected void draw(Model mod, Renderable tex) {
		this.bind();
		this.setUniformMatrix4f("projectionMat",camera.getProjection());
		this.setUniformMatrix4f("viewMat", new Matrix4f(camera.getViewMatrix()).m30(0).m31(0).m32(0));
		this.setUniformMatrix4f("transformationMat",Tools.IDENTITY_MATRIX);
		if(tex != null)
			tex.bind(0);
		else
			this.setUniformBool("dynamic", true);
		mod.render();
		this.bindNullShader();
	}
	
	@Override
	public void draw(GEntity gentity) {
		this.bind();
		this.setUniformMatrix4f("projectionMat",camera.getProjection());
		this.setUniformMatrix4f("viewMat", new Matrix4f(camera.getViewMatrix()).m30(0).m31(0).m32(0));
		this.setUniformMatrix4f("transformationMat",gentity.getTransformationMatrix());
		if(gentity.getTex() != null)
			gentity.getTex().bind(0);
		else
			this.setUniformBool("dynamic", true);
		gentity.getMod().render();
		this.bindNullShader();
	}
	
	@Override
	protected void setUniforms(GEntity entity) {
		this.bind();
		this.setUniformBool("dynamic", dynamic);
		this.setUniformVec3("up", up);
		this.setUniformVec3("wavelenghts", wavelenghts);
		this.setUniformVec3("sun", sun);
		this.setUniformFloat("tw", tw);
		this.setUniformFloat("ss", ss);
		this.setUniformFloat("sb", sb);
		this.setUniformFloat("fl", fl);
		this.setUniformVec3("n_stars", n_stars);
		this.setUniformVec3("n_color", n_color);
		this.setUniformVec3("n_lines", n_lines);
		this.setUniformVec3("n_moon", n_moon);
		this.setUniformBool("cool", cool);
		if(sun_dir != null)
			this.setUniformVec3("DirectionalLight.direction", sun_dir);
	}
	
	public SkyboxShader setCamera(Camera camera) {
		this.camera = camera;
		return this;
	}
	
	public static Vector3f getGlobalBrighness(Vector3f sunDir, Vector3f up) {
		float d = Math.max(-sunDir.dot(up), -0.1f);
		float a = (float) (Math.pow((1-1/(d*d+1)), 0.001)*1.5);
		float r = (float) (-Math.pow((2*d-.5f), 2f)+.2f)*.5f+.5f;
		return new Vector3f(a+r, a+r/2, a).mul(d < 0 ? 0 : 1);
	}

	public boolean isDynamic() {
		return dynamic;
	}

	public SkyboxShader setDynamic(boolean dynamic) {
		this.dynamic = dynamic;
		return this;
	}

	public Vector3f getUp() {
		return up;
	}

	public SkyboxShader setUp(Vector3f up) {
		this.up = up;
		return this;
	}

	public Vector3f getWavelenghts() {
		return wavelenghts;
	}

	public SkyboxShader setWavelenghts(Vector3f wavelenghts) {
		this.wavelenghts = wavelenghts;
		return this;
	}

	public float getTw() {
		return tw;
	}

	public SkyboxShader setTw(float tw) {
		this.tw = tw;
		return this;
	}

	public float getSs() {
		return ss;
	}

	public SkyboxShader setSs(float ss) {
		this.ss = ss;
		return this;
	}

	public float getSb() {
		return sb;
	}

	public SkyboxShader setSb(float sb) {
		this.sb = sb;
		return this;
	}

	public float getFl() {
		return fl;
	}

	public SkyboxShader setFl(float fl) {
		this.fl = fl;
		return this;
	}

	public Vector3f getN_lines() {
		return n_lines;
	}
	
	public SkyboxShader setN_lines(Vector3f n_lines) {
		this.n_lines = n_lines;
		return this;
	}
	
	public Vector3f getN_stars() {
		return n_stars;
	}
	
	public SkyboxShader setN_stars(Vector3f n_stars) {
		this.n_stars = n_stars;
		return this;
	}

	public Vector3f getN_color() {
		return n_color;
	}

	public SkyboxShader setN_color(Vector3f n_color) {
		this.n_color = n_color;
		return this;
	}

	public Camera getCamera() {
		return camera;
	}

	public Vector3f getSun() {
		return sun;
	}

	public SkyboxShader setSun(Vector3f sun) {
		this.sun = sun;
		return this;
	}

	public Vector3f getN_moon() {
		return n_moon;
	}

	public SkyboxShader setN_moon(Vector3f n_moon) {
		this.n_moon = n_moon;
		return this;
	}
	public Vector3f getSun_dir() {
		return sun_dir;
	}
	public SkyboxShader setSun_dir(Vector3f sun_dir) {
		this.sun_dir = sun_dir;
		return this;
	}
	public SkyboxShader setCool(boolean cool) {
		this.cool = cool;
		return this;
	}
	public boolean isCool() {
		return cool;
	}
	

}
