package Kartoffel.Licht.JGL.Components;

import Kartoffel.Licht.JGL.JGLComponent;
import Kartoffel.Licht.JGL.JGLFI;
import Kartoffel.Licht.Java.Color;
import Kartoffel.Licht.Rendering.Shaders.TextShader;
import Kartoffel.Licht.Rendering.Text.FontProvider;
import Kartoffel.Licht.Rendering.Text.GlobalFont;
import Kartoffel.Licht.Rendering.Text.Text;

public class JGLTextField extends JGLComponent{

	public static TextShader shader;
	public static FontProvider f;
	
	private Text text;
	
	private int textPosX = 0;
	private int textPosY = 0;
	private float[] sizeMod = new float[] {0.5f, 0.5f};
	private int staticSize = -1;
	private boolean doClipping = true;
	
	public static void initFont(FontProvider font) {
		f = font;
	}
	
	public JGLTextField(String text) {
		super(100, 100, 500, 500);
		if(shader == null) {
			shader = new TextShader(null);
		}
		if(f == null) {
			if(GlobalFont.font == null)
				throw new RuntimeException("No font currently loaded!");
			else
				f = GlobalFont.font;
		}
		this.setCursor(0);
		this.text = new Text(f);
		this.text.setText(text);
		this.text.setSize(1);
	}
	
	
	final public void setText(String text) {
		this.text.setText(text);
	}
	
	final public Text getText() {
		return text;
	}
	
	final public void setSelection(int a, int b) {
		text.setSelectedText(a, b);
	}
	int x, y;
	@Override
	protected void paintComponent(JGLFI info, int xoff, int yoff) {
		getText().setText(updateContent());
		x = (int) ((getBounds().x+xoff)+getBounds().width*(textPosX*.5f+.5));
		y = (int) ((getBounds().y+yoff)+getBounds().height*(textPosY*.5f+.5));
		text.getPosition().x = ((float)(x)/info.sx*2+bx);
		text.getPosition().y = -((float)(y)/info.sy*2+by);
		float s = staticSize;
		if(staticSize == -1)
			s = bounds.height*sizeMod[1]+bounds.width*sizeMod[0];
		text.getScale().x = s/info.sx;
		text.getScale().y = s/info.sy;
		text.setNewLineJump(s/2);
		if(doClipping) {
			place(text.getClip(), info, xoff, yoff);
			text.getClip().x -= text.getClip().z/2;
			text.getClip().y -= text.getClip().w/2;
			text.getClip().z = text.getClip().x+text.getClip().z;
			text.getClip().w = text.getClip().y+text.getClip().w;
		}
		shader.render(text);
	}
	
	@Override
	protected void disposeComponent() {
		text.free();
		text = null;
	}
	public String updateContent() {
		return getText().getText();
	}
	
	public void setColor(Color color) {
		text.setColor(color);
	}

	public static void free() {
		if(shader != null)
			shader.free();
	}
	
	public JGLTextField setTextPosX(int textPosX) {
		this.textPosX = textPosX;
		return this;
	}
	public int getTextPosX() {
		return textPosX;
	}
	public JGLTextField setTextPosY(int textPosY) {
		this.textPosY = textPosY;
		return this;
	}
	public int getTextPosY() {
		return textPosY;
	}
	public int getStaticSize() {
		return staticSize;
	}
	public JGLTextField setStaticSize(int staticSize) {
		this.staticSize = staticSize;
		return this;
	}
	public JGLTextField setDoClipping(boolean doClipping) {
		this.doClipping = doClipping;
		return this;
	}
	public boolean isDoClipping() {
		return doClipping;
	}
	public float[] getSizeMod() {
		return sizeMod;
	}
	public void setSizeMod(float modX, float modY) {
		this.sizeMod[0] = modX;
		this.sizeMod[1] = modY;
	}
	
}
