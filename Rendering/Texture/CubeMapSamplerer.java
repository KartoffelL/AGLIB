package Kartoffel.Licht.Rendering.Texture;

import static org.lwjgl.opengl.GL11.glDrawBuffer;
import static org.lwjgl.opengl.GL11.glReadBuffer;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;

import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL33;

import Kartoffel.Licht.Engine.JApp;
import Kartoffel.Licht.Rendering.Camera;
import Kartoffel.Licht.Rendering.FrameBuffer;
import Kartoffel.Licht.Rendering.GraphicWindow;

public class CubeMapSamplerer {

	private CubeMap cubeMap;
	private Camera cam;
	private FrameBuffer fbo;
	private boolean onlyEntities = true;
	public CubeMapSamplerer(CubeMap cubeMap, Camera cam, FrameBuffer fbo) {
		super();
		this.cubeMap = cubeMap;
		this.cam = cam;
		this.fbo = fbo;
	}
	public CubeMapSamplerer(GraphicWindow window, int resolution) {
		this.cubeMap = new CubeMap(resolution);
		this.cam = new Camera();
		this.fbo = new FrameBuffer(window, resolution, resolution, 0);
	}
	public void sample(JApp app) {
		sample(new Runnable() {
			
			@Override
			public void run() {
				try {
					app.paintAll(fbo, cam, true, false, onlyEntities);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
	public void sample(JApp app, Runnable draw) {
		sample(new Runnable() {
			
			@Override
			public void run() {
				try {
					app.helpPaint(fbo, cam, draw);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
	public void sample(Runnable draw) {
		try {
			fbo.bind();
			fbo.resetViewPortSize();
			this.cam.getProjectionBox().setFOV(90);
			this.cam.getProjectionBox().setAspectRatio(1);
			this.cam.getPosition().y = 1;
			this.cam.setDistance(0);
			
			glDrawBuffer(GL_COLOR_ATTACHMENT0);
			glReadBuffer(GL_COLOR_ATTACHMENT0);
			GL33.glFramebufferTexture2D(GL33.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL33.GL_TEXTURE_CUBE_MAP_POSITIVE_X, cubeMap.getID(0), 0);
			cam.getRotation().y -= 0;
			cam.update();
			draw.run();
			
			glDrawBuffer(GL_COLOR_ATTACHMENT0);
			glReadBuffer(GL_COLOR_ATTACHMENT0);
			GL33.glFramebufferTexture2D(GL33.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL33.GL_TEXTURE_CUBE_MAP_POSITIVE_Z, cubeMap.getID(0), 0);
			cam.getRotation().y -= 90;
			cam.update();
			draw.run();
			
			glDrawBuffer(GL_COLOR_ATTACHMENT0);
			glReadBuffer(GL_COLOR_ATTACHMENT0);
			GL33.glFramebufferTexture2D(GL33.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL33.GL_TEXTURE_CUBE_MAP_NEGATIVE_X, cubeMap.getID(0), 0);
			cam.getRotation().y -= 90;
			cam.update();
			draw.run();
			
			glDrawBuffer(GL_COLOR_ATTACHMENT0);
			glReadBuffer(GL_COLOR_ATTACHMENT0);
			GL33.glFramebufferTexture2D(GL33.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL33.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, cubeMap.getID(0), 0);
			cam.getRotation().y -= 90;
			cam.update();
			draw.run();
			
			glDrawBuffer(GL_COLOR_ATTACHMENT0);
			glReadBuffer(GL_COLOR_ATTACHMENT0);
			GL33.glFramebufferTexture2D(GL33.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL33.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, cubeMap.getID(0), 0);
			cam.getRotation().y -= 180;
			cam.getRotation().x -= 90;
			cam.update();
			draw.run();
			
			glDrawBuffer(GL_COLOR_ATTACHMENT0);
			glReadBuffer(GL_COLOR_ATTACHMENT0);
			GL33.glFramebufferTexture2D(GL33.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL33.GL_TEXTURE_CUBE_MAP_POSITIVE_Y, cubeMap.getID(0), 0);
			cam.getRotation().y -= 0;
			cam.getRotation().x -= 180;
			cam.update();
			draw.run();
			
			cam.getRotation().y += 90;
			cam.getRotation().x -= 90;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public CubeMap getCubeMap() {
		return cubeMap;
	}
	public void setCubeMap(CubeMap cubeMap) {
		this.cubeMap = cubeMap;
	}
	public Camera getCam() {
		return cam;
	}
	public void setCam(Camera cam) {
		this.cam = cam;
	}
	public FrameBuffer getFbo() {
		return fbo;
	}
	public void setFbo(FrameBuffer fbo) {
		this.fbo = fbo;
	}
	public boolean isOnlyEntities() {
		return onlyEntities;
	}
	public void setOnlyEntities(boolean onlyEntities) {
		this.onlyEntities = onlyEntities;
	}
}
