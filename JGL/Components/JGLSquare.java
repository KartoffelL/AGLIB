package Kartoffel.Licht.JGL.Components;

import Kartoffel.Licht.JGL.JGLComponent;
import Kartoffel.Licht.JGL.JGLFI;
import Kartoffel.Licht.Java.BufferedImage;
import Kartoffel.Licht.Java.Color;
import Kartoffel.Licht.Rendering.GEntity;
import Kartoffel.Licht.Rendering.Model;
import Kartoffel.Licht.Rendering.Shaders.Shader;
import Kartoffel.Licht.Rendering.Shapes.SBox2D;
import Kartoffel.Licht.Rendering.Texture.Renderable;
import Kartoffel.Licht.Rendering.Texture.Texture;
import Kartoffel.Licht.Res.FileLoader;

public class JGLSquare extends JGLComponent{

	private static Shader shader;
	private static Model m;
	private Renderable tex1;
	
	private GEntity gent;
	
	private boolean alwaysOnTop = false;
	private boolean absolute = false;
	private float radius = 5; //Pixels
	private float edgeSoftness = 1f; //Pixels
	private float areaExtent = 1; //Pixels
	
	public JGLSquare(Color c) {
		super(200, 200, 500, 500);
		if(shader == null) {
			shader = new Shader(FileLoader.readFileD("default/shaders/basic2D.vert"), FileLoader.readFileD("default/shaders/jgl/basic2D.frag"), "JGL default Shader");
			m = new Model(new SBox2D(0.5f, 0.5f));
		}
		tex1 = new Texture(c);
		gent = new GEntity(tex1, m);
	}
	
	public JGLSquare(BufferedImage bi) {
		super(200, 200, 500, 500);
		if(shader == null) {
			shader = new Shader(FileLoader.readFileD("default/shaders/basic2D.vert"), FileLoader.readFileD("default/shaders/jgl/basic2D.frag"), "JGL default Shader");
			m = new Model(new SBox2D(0.5f, 0.5f));
		}
		tex1 = new Texture(bi);
		gent = new GEntity(tex1, m);
	}
	
	public JGLSquare(Renderable ren) {
		super(200, 200, 500, 500);
		if(shader == null) {
			shader = new Shader(FileLoader.readFileD("default/shaders/basic2D.vert"), FileLoader.readFileD("default/shaders/jgl/basic2D.frag"), "JGL default Shader");
			m = new Model(new SBox2D(0.5f, 0.5f));
		}
		tex1 = ren;
		gent = new GEntity(tex1, m);
	}
	
	final public void setTexture(Renderable tex1) {
		this.tex1 = tex1;
		this.gent.setTex(tex1);
	}
	
	final public Renderable getTexture() {
		return tex1;
	}
	
	@Override
	protected void paintComponent(JGLFI info, int xoff, int yoff) {
		this.bounds.x-= areaExtent;
		this.bounds.y-= areaExtent;
		this.bounds.width+= areaExtent*2;
		this.bounds.height+= areaExtent*2;
		if(!absolute)
			place(gent, info, xoff, yoff);
		if(alwaysOnTop)
			gent.getPosition().z = 0;
		this.bounds.x+= areaExtent;
		this.bounds.y+= areaExtent;
		this.bounds.width-= areaExtent*2;
		this.bounds.height-= areaExtent*2;
		shader.bind();
		shader.setUniformMatrix4f("transformationMat", gent.getTransformationMatrix());
		shader.setUniformFloat("radius", radius);
		shader.setUniformFloat("edgeSoftness", edgeSoftness);
		shader.setUniformVec2("size", bounds.width, bounds.height);
		shader.setUniformVec2("offset", areaExtent, areaExtent);
		shader.setUniformVec2("mul", bounds.width+areaExtent*2, bounds.height+areaExtent*2);
		shader.render(gent);
	}

	@Override
	protected void disposeComponent() {
		gent = null;
		tex1.free();
		tex1 = null;
	}
	
	
	final public GEntity getBox() {
		return gent;
	}
	
	final public boolean isAlwaysOnTop() {
		return alwaysOnTop;
	}
	
	final public JGLSquare setAlwaysOnTop(boolean alwaysOnTop) {
		this.alwaysOnTop = alwaysOnTop;
		return this;
	}
	
	final public Shader getShader() {
		return shader;
	}

	public static void free() {
		if(shader != null)
			shader.free();
		if(m != null)
			m.free();
	}
	
	public boolean isAbsolute() {
		return absolute;
	}
	public JGLSquare setAbsolute(boolean absolute) {
		this.absolute = absolute;
		return this;
	}
	public GEntity getGent() {
		return gent;
	}
	public float getRounding() {
		return radius;
	}
	public JGLSquare setRounding(float rounding) {
		this.radius = rounding;
		return this;
	}
	public float getEdgeSoftness() {
		return edgeSoftness;
	}
	public JGLSquare setEdgeSoftness(float edgeSoftness) {
		this.edgeSoftness = edgeSoftness;
		return this;
	}
	public float getAreaExtent() {
		return areaExtent;
	}
	public void setAreaExtent(float areaExtent) {
		this.areaExtent = areaExtent;
	}
	
}
