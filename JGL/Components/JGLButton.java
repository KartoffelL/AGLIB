package Kartoffel.Licht.JGL.Components;


import Kartoffel.Licht.JGL.JGLComponent;
import Kartoffel.Licht.JGL.JGLFI;
import Kartoffel.Licht.Java.Color;
import Kartoffel.Licht.Rendering.Texture.Renderable;
import Kartoffel.Licht.Rendering.Texture.Texture;

public class JGLButton extends JGLComponent{

	private Renderable tex1;
	private Renderable tex2;
	private Renderable tex3;
	
	
	JGLSquare square;
	JGLTextField text;
	boolean click = false;
	
	public JGLButton() {
		super(10, 10, 200, 500);
		init("");
		this.setCursor(3);
	}
	
	public JGLButton(String name) {
		super(0, 0, 200, 500);
		init(name);
		this.setCursor(3);
	}
	
	
	final private void init(String name) {
		text = new JGLTextField(name);
		text.setColor(Color.WHITE);
		text.getText().setOriginX(0).setOriginY(2);
		tex1 = new Texture(Color.GRAY);
		tex2 = new Texture(Color.GRAY.darker().darker());
		tex3 = new Texture(Color.GRAY.darker());
		square = new JGLSquare(tex1);
		this.add(square);
		this.add(text);
	}
	
	@Override
	protected void paintComponent(JGLFI info, int xoff, int yoff) {
		if(isHovered())
			if(isClicking())
				square.setTexture(tex2);
			else
				square.setTexture(tex3);
		else
			square.setTexture(tex1);
		
		square.setBounds(0, 0, bounds.width, bounds.height);
		text.setBounds(0, 0, bounds.width, bounds.height);
	}
	
	@Override
	protected void MouseClickComponent(JGLFI info, int x, int y, int button, boolean down, boolean focus) {
		if(down && focus)
			onActionEvent();
	}
	
	protected void onActionEvent() {
		
	}
	
	public JGLTextField getText() {
		return text;
	}

	public static void free() {
		// TODO Auto-generated method stub
		
	}

}
