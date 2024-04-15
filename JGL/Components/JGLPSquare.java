package Kartoffel.Licht.JGL.Components;

import Kartoffel.Licht.JGL.JGLComponent;
import Kartoffel.Licht.JGL.JGLFI;
import Kartoffel.Licht.Rendering.GEntity;
import Kartoffel.Licht.Rendering.Model;
import Kartoffel.Licht.Rendering.Shaders.BasicShader2D;
import Kartoffel.Licht.Rendering.Shaders.Shader;
import Kartoffel.Licht.Rendering.Shapes.SBox2D;
import Kartoffel.Licht.Rendering.Texture.Renderable;

public class JGLPSquare extends JGLComponent{

	private Shader shader;
	private static Model m;
	private Renderable tex1;
	
	private GEntity gent;
	
	private boolean alwaysOnTop = false;
	
	
	public JGLPSquare(Renderable ren) {
		super(200, 200, 500, 500);
		if(shader == null) {
			shader = new BasicShader2D(true) {
				@Override
				protected void setUniforms(GEntity entity) {
					uniforms(shader);
				}
			};
			m = new Model(new SBox2D(0.5f, 0.5f));
		}
		tex1 = ren;
		gent = new GEntity(tex1, m);
	}
	
	protected void uniforms(Shader shader) {
		
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
		place(gent, info);
		if(alwaysOnTop)
			gent.getPosition().z = 0;
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
	
	final public void setAlwaysOnTop(boolean alwaysOnTop) {
		this.alwaysOnTop = alwaysOnTop;
	}
	
	final public Shader getShader() {
		return shader;
	}

	public static void free() {
		if(m != null)
			m.free();
	}
	
}
