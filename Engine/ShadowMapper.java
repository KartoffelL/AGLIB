package Kartoffel.Licht.Engine;

import java.util.List;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL33;

import Kartoffel.Licht.Java.freeable;
import Kartoffel.Licht.Rendering.Camera;
import Kartoffel.Licht.Rendering.FrameBuffer;
import Kartoffel.Licht.Rendering.GEntity;
import Kartoffel.Licht.Rendering.Shaders.Shader;
import Kartoffel.Licht.Rendering.Shaders.ShadowShader;
import Kartoffel.Licht.Rendering.Texture.FBOTexture;

public class ShadowMapper implements freeable{
	
	private int cascades = 4; //max cascades supported 4
	private int mapSize = 4096; //Total Texture resolution will then be mapSize²cascades
	private double CascadeSpreading = 3;
	
	
	private FrameBuffer fb;
	private Camera camera;
	
	private Camera shadow_cam;
	private Matrix4f matrice;
	private Vector3f[] positions;
	private float[] siz;
	
	private ShadowShader shadowShader;

	public ShadowMapper(Camera cam) {
		fb = new FrameBuffer(null, mapSize*cascades, mapSize, 0);
		shadow_cam = new Camera(1);
		positions = new Vector3f[cascades];
		matrice = new Matrix4f();
		for(int i = 0; i < cascades; i++)
			positions[i] = new Vector3f();
		siz = new float[cascades];
		shadowShader = new ShadowShader(shadow_cam);
		camera = cam;
	}
	
	public ShadowMapper(Camera cam, int cascades, int mapSize, double cascadeSpreading) {
		fb = new FrameBuffer(null, mapSize*cascades, mapSize, 0);
		shadow_cam = new Camera(1);
		positions = new Vector3f[cascades];
		matrice = new Matrix4f();
		for(int i = 0; i < cascades; i++)
			positions[i] = new Vector3f();
		siz = new float[cascades];
		shadowShader = new ShadowShader(shadow_cam);
		camera = cam;
		if(cascades > 4 || cascades < 0)
			throw new IllegalArgumentException("Number of cascades has to be in between 0..4!");
		this.cascades = cascades;
		this.mapSize = mapSize;
		this.CascadeSpreading = cascadeSpreading;
	}
	
	public float sizeEq(int i, float size) {
		return distEqu(i, size);
	}
	public float distEqu(int i, float size) {
		return (float) Math.pow(CascadeSpreading, i)*size;
	}
	 
	public void render(List<GEntity> entities) {
		shadow_cam.getProjectionBox().setPlaneSize(1);
		shadow_cam.getPosition().set(0);
		shadow_cam.update();
		shadow_cam.getProViewMatrix().get(matrice);
		fb.clear();
		for(int i = 0; i < cascades; i++) {  
			GL33.glCullFace(GL33.GL_FRONT);
			float size = 20;
			float isize = sizeEq(i, size);
			float idist = distEqu(i, size);
			shadow_cam.getProjectionBox().setPlaneSize(isize);
			shadow_cam.getPosition().set(camera.getPositionDistanced());
			shadow_cam.getPosition().add(camera.getOutDirection().mul(-idist));
			shadow_cam.getProjectionBox().setZ_NEAR(-500);
			shadow_cam.getProjectionBox().setZ_FAR(500);
			shadow_cam.update();
			positions[i].set(shadow_cam.getPosition());
			siz[i] = isize;
			FrameBuffer.setViewPortSize(mapSize*i, 0, mapSize, mapSize);
			shadowShader.render(entities);
			GL33.glCullFace(GL33.GL_BACK);
		}
	}
	
	public Camera getShadow_cam() {
		return shadow_cam;
	}
	
	
	public FBOTexture getTexture() {
		return fb.getDepthStencilTexture();
	}
	
	public Matrix4f getMatrix() {
		return matrice;
	}
	
	public Vector3f getPosition(int index) {
		return positions[index];
	}
	public float getSize(int index) {
		return siz[index];
	}
	
	public int getCascades() {
		return cascades;
	}
	
	public int getMapSize() {
		return mapSize;
	}
	
	@Override
	public void free() {
		fb.free();
	}

	public void setUniforms(Shader s) {
		s.setUniformMatrix4f("shadowpv", matrice);
		for(int i = 0; i < cascades; i++) {
			s.setUniformVec3("shadowpos["+i+"]", positions[i]);
		}
		for(int i = 0; i < cascades; i++) {
			s.setUniformFloat("shadowsiz["+i+"]", siz[i]);
		}
		s.setUniformInt("cascades", cascades);
		s.setUniformFloat("shadowMapRes", getMapSize());
	}
	
	

}
