package Kartoffel.Licht.Engine.D2;

import java.util.ArrayList;
import java.util.List;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.lwjgl.opengl.GL33;

import Kartoffel.Licht.Java.freeable;
import Kartoffel.Licht.Rendering.FrameBuffer;
import Kartoffel.Licht.Rendering.GraphicWindow;
import Kartoffel.Licht.Rendering.Model;
import Kartoffel.Licht.Rendering.Shaders.BasicShader2D;
import Kartoffel.Licht.Rendering.Shaders.PostShader;
import Kartoffel.Licht.Rendering.Shapes.SBox2D;
import Kartoffel.Licht.Rendering.Texture.FBOTexture;
import Kartoffel.Licht.Rendering.Texture.Renderable;
import Kartoffel.Licht.Tools.Timer;

public class Canvas implements freeable{
	
	static class CanvasPainter {
		PostShader ps;
		Renderable tex;
		GraphicWindow window;
		public CanvasPainter(Renderable tex, GraphicWindow window) {
			this.ps = new  PostShader(PostShader.POST_SHADER_PASS);
			this.tex = tex;
			this.window = window;
		}
		public void paint() {
			GraphicWindow.doPollEvents();
			
			window.bindFrameBuffer(null);
			ps.render(tex);
			window.updateWindow(true);
		}
	}
	
	public static class actor {
		public Vector2f position = new Vector2f();
		public Vector2f velocity = new Vector2f();
		public Vector2f scale = new Vector2f(1);
		public float rotation = 0;
		public boolean absolutePos = false;
		public boolean absoluteRot = false;
		public boolean absoluteScale = false;
		public boolean bstatic = false;
		
		public Renderable texture;

		public actor(Renderable texture) {
			this.texture = texture;
		}
		public actor(Renderable texture, float x, float y, float sx, float sy) {
			this.texture = texture;
			this.position.set(x, y);
			this.scale.set(sx, sy);
		}
		public actor(Renderable texture, Vector2f position, float sx, float sy) {
			this.texture = texture;
			this.position = position;
			this.scale.set(sx, sy);
		}
	}
	
	private FrameBuffer framebuffer;
	private CanvasPainter painter;
	private BasicShader2D shader;
	private Matrix4f matrix;
	private Model model;
	private List<actor> actors;
	
	private Vector2f camPos = new Vector2f();
	private float camRot = 0;
	private float camScale = 0.1f;
	
	private int aspectRatioRatioFlipType = 2;
	private Vector2i windowSize = new Vector2i();
	private boolean adaptWindowSize = false;
	
	public Canvas(GraphicWindow window, int sizeX, int sizeY) {
		this.actors = new ArrayList<Canvas.actor>();
		this.framebuffer = new FrameBuffer(window, sizeX, sizeY, 1, FrameBuffer.FLAG_AUTORESIZE);
		this.painter = new CanvasPainter(framebuffer.getTexture(), window);
		this.shader = new BasicShader2D();
		this.model = new Model(new SBox2D(1, 1));
		this.matrix = new Matrix4f();
		this.windowSize.set(1, 1);
	}
	
	void updateActor(actor a, float delta) {
		if(a.bstatic)
			a.position.add(a.velocity.x*delta, a.velocity.y*delta);
	}
	double lastTime = Timer.getTimeSeconds();
	public double delta;
	public void update(double deltaMul) {
		delta = Timer.getTimeSeconds()-lastTime;
		lastTime = Timer.getTimeSeconds();
		
		for(actor a : actors) {
			updateActor(a, (float) delta);
		}
		
	}
	public void paint() {
		framebuffer.clear();
		GL33.glDisable(GL33.GL_DEPTH_TEST);
		
		if(adaptWindowSize)
			windowSize.set(painter.window.getWidth(), painter.window.getHeight());
		float aspect = painter.window.getWindowAspectRatio();
		float aspectX = 1;
		float aspectY = 1;
		if(aspectRatioRatioFlipType == 0)
			aspectX = 1/aspect;
		else if(aspectRatioRatioFlipType == 1)
			aspectY = aspect;
		else
			if(aspect > 1)
				aspectX = 1/aspect;
			else
				aspectY = aspect;
		
		for(actor a : actors) {
			//Rendering
			if(a.texture != null) {
				shader.bind();
				matrix.identity();
				if(!a.absolutePos)
					matrix.translate(-camPos.x, -camPos.y, 0);
				if(!a.absoluteRot)
					matrix.rotateXYZ(-camRot, 0, 0);
				if(!a.absoluteScale)
					matrix.scale(camScale);
				matrix.translate(a.position.x, a.position.y, 0);
				matrix.rotateXYZ(a.rotation, 0, 0);
				matrix.scale(a.scale.x*aspectX, a.scale.y*aspectY, 1);
				
				
				shader.setUniformMatrix4f("transformationMat", matrix);
				shader.render(model, a.texture);
			}
		}
		GL33.glEnable(GL33.GL_DEPTH_TEST);
		painter.paint();
	}
	
	public void free() {
		this.model.free();
		this.shader.free();
		this.painter.ps.free();
		this.framebuffer.free();
	}
	
	public FBOTexture getTexture() {
		return framebuffer.getTexture();
	}

	public List<actor> getActors() {
		return actors;
	}
	
	public int getAspectRatioRatioFlipType() {
		return aspectRatioRatioFlipType;
	}
	
	public void setAspectRatioRatioFlipType(int aspectRatioRatioFlipType) {
		this.aspectRatioRatioFlipType = aspectRatioRatioFlipType;
	}
	public Vector2f getCamPos() {
		return camPos;
	}
	public float getCamRot() {
		return camRot;
	}
	public float getCamScale() {
		return camScale;
	}
	public void setCamScale(float camScale) {
		this.camScale = camScale;
	}
	
}
