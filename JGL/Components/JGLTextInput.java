package Kartoffel.Licht.JGL.Components;

import org.joml.Vector4f;

import Kartoffel.Licht.Input.TextInputInstance;
import Kartoffel.Licht.JGL.JGLComponent;
import Kartoffel.Licht.JGL.JGLFI;
import Kartoffel.Licht.Java.Color;

public class JGLTextInput extends JGLComponent{

	JGLTextField text;
	JGLSquare square;
	TextInputInstance in;
	
	
	public JGLTextInput() {
		super(10, 10, 500, 500);
		text = new JGLTextField("text");
		text.setColor(Color.WHITE);
		square = new JGLSquare(Color.BLACK);
		in = new TextInputInstance();
		this.add(square);
		this.add(text);
		text.setTextPosX(0).setTextPosY(0).setStaticSize(50);
	}
	
	int[] selected = new int[2];
	
	@Override
	final protected void paintComponent(JGLFI info, int xoff, int yoff) {
		if(in != null) {
			text.setText(in.getText());
			selected = in.getSelection();			text.setSelection(selected[0], selected[1]);
		}
		square.setBounds(0, 0, bounds.width, bounds.height);
		text.setBounds(0, 0, bounds.width, bounds.height);
		Vector4f p1 = text.getText().getLow();
		Vector4f p2 = text.getText().getHigh();
		p1.x = Math.max(text.getText().getClip().x, Math.min(text.getText().getClip().z, p1.x));
		p2.x = Math.max(text.getText().getClip().x, Math.min(text.getText().getClip().z, p2.x));
		if(in.shouldDrawIndicator()) {
			text.getText().setSelectorWidth(3.0f/info.sx);
		}
		text.getText().setLowIndA(in.getSelect_1());
		text.getText().setLowIndB(in.getSelect_2());
		if(info.window.getCallback_Key().isKeyPressed("Enter"))
			onActionEvent(text.getText().getText());
	}
	
	protected void onActionEvent(String text) {
		
	}
	
	
	@Override
	final protected void MouseClickComponent(JGLFI info, int x, int y, int button, boolean down, boolean hoverd) {
		if(isInBounds(info) && hoverd) {
			info.window.getCallback_Text().setCurrentInstance(in);
			in.startWriting();
			this.setCursor(5);
		}else {
			in.stopWriting();
			this.setCursor(0);
		}
	}
	
	final public TextInputInstance getText() {
		return in;
	}
	final public JGLTextField getTextComp() {
		return text;
	}

	public static void free() {
		// TODO Auto-generated method stub
		
	}

}
