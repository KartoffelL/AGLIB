package Kartoffel.Licht.JGL.Components;

import Kartoffel.Licht.JGL.JGLComponent;
import Kartoffel.Licht.JGL.JGLFI;
import Kartoffel.Licht.Java.BufferedImage;
import Kartoffel.Licht.Java.Color;
import Kartoffel.Licht.Rendering.GEntity;
import Kartoffel.Licht.Rendering.Model;
import Kartoffel.Licht.Rendering.Shaders.BasicShader2D;
import Kartoffel.Licht.Rendering.Shapes.SBox2D;
import Kartoffel.Licht.Rendering.Texture.Renderable;
import Kartoffel.Licht.Rendering.Texture.Texture;

public class JGLBackground extends JGLComponent{

	private static BasicShader2D shader;
	private static Model m;
	private Renderable tex1;
	
	private GEntity gent;
	
	
	public JGLBackground(Color c) {
		super(0, 0, 2000, 2000);
		if(shader == null) {
			shader = new BasicShader2D(true);
			m = new Model(new SBox2D(1, 1));
		}
		tex1 = new Texture(c);
		gent = new GEntity(tex1, m);
		this.setCursor(0);
	}
	
	public JGLBackground(BufferedImage bi) {
		super(0, 0, 2000, 2000);
		if(shader == null) {
			shader = new BasicShader2D(true);
			m = new Model(new SBox2D(1, 1));
		}
		tex1 = new Texture(bi);
		gent = new GEntity(tex1, m);
		this.setCursor(0);
	}
	
	public JGLBackground(Renderable ren) {
		super(0, 0, 2000, 2000);
		if(shader == null) {
			shader = new BasicShader2D(true);
			m = new Model(new SBox2D(1, 1));
		}
		tex1 = ren;
		gent = new GEntity(tex1, m);
		this.setCursor(0);
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

	public static void free() {
		if(shader != null)
			shader.free();
		if(m != null)
			m.free();
	}
	
	
}
