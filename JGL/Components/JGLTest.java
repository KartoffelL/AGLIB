package Kartoffel.Licht.JGL.Components;

import Kartoffel.Licht.JGL.JGLComponent;
import Kartoffel.Licht.JGL.JGLFI;
import Kartoffel.Licht.Java.Color;
import Kartoffel.Licht.Rendering.GEntity;
import Kartoffel.Licht.Rendering.Model;
import Kartoffel.Licht.Rendering.Shaders.BasicShader2D;
import Kartoffel.Licht.Rendering.Shapes.SBox2D;
import Kartoffel.Licht.Rendering.Texture.Renderable;
import Kartoffel.Licht.Rendering.Texture.Texture;
import Kartoffel.Licht.Tools.TextureUtils;

public class JGLTest extends JGLComponent{

	private static BasicShader2D shader;
	private static Model m;
	private static Renderable tex1;
	private static Renderable tex2;
	
	private GEntity gent;
	
	public JGLTest() {
		super(200, 200, 500, 500);
		if(shader == null) {
			shader = new BasicShader2D(true);
			m = new Model(new SBox2D(0.5f, 0.5f));
			tex1 = new Texture(TextureUtils.generateColorTransition(Color.BLUE.darker(), Color.RED.darker(), 16, 1));
			tex2 = new Texture(TextureUtils.generateColorTransition(Color.BLUE, Color.RED, 16, 1));
		}
		gent = new GEntity(tex1, m);
	}
	
	@Override
	final protected void paintComponent(JGLFI info, int xoff, int yoff) {
		place(gent, info);
		if(isInBounds(info))
			gent.setTex(tex2);
		else
			gent.setTex(tex1);
		shader.render(gent);
	}

	public static void free() {
		// TODO Auto-generated method stub
		if(shader != null)
			shader.free();
		if(m != null)
			m.free();
		if(tex1 != null)
			tex1.free();
		if(tex2 != null)
			tex2.free();
	}

	
	
	
}
