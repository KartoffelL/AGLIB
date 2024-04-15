package Kartoffel.Licht.Test;

import Kartoffel.Licht.Input.Formatting;
import Kartoffel.Licht.JGL.JGLFrame;
import Kartoffel.Licht.JGL.Components.JGLBackground;
import Kartoffel.Licht.JGL.Components.JGLButton;
import Kartoffel.Licht.JGL.Components.JGLTextField;
import Kartoffel.Licht.JGL.Components.JGLTextInput;
import Kartoffel.Licht.Java.Color;
import Kartoffel.Licht.Rendering.GraphicWindow;
import Kartoffel.Licht.Rendering.Text.GlobalFont;
import Kartoffel.Licht.Rendering.Text.Text;
import Kartoffel.Licht.Tools.Logger;
import Kartoffel.Licht.Tools.TextureUtils;
import Kartoffel.Licht.Tools.Tools;

public class Debug {
	Debug() {}
	
	private static boolean bold=false, italic=false;
	public static void main(String[] args) {
		Logger.log("Initializing Demo..");
		GraphicWindow.GLFW_RESIZABLE = true;
		JGLFrame frame = new JGLFrame("AGLibrary Demo");
		GlobalFont.init();
		frame.setIcon(TextureUtils.generateCircle(Color.GRAY, 64, 3));
		frame.setSize(500, 700);
		JGLTextField text = null;
		frame.add(new JGLBackground(Color.GRAY));
		frame.add(text = (JGLTextField) new JGLTextField("text") {
			public String updateContent() {
				return Logger.log;
			};
		}.setBounds(0, 0, 500, 500));
		text.getText().setOriginX(1);
		text.getText().setOriginY(1);
		text.setTextPosX(-1);
		text.setTextPosY(-1);
		text.getText().setSize(0.002f);
		JGLTextInput i;
		frame.add((i = new JGLTextInput() {
			protected void onActionEvent(String text) {
				if(text.length() != 0) {
					Logger.log(text, Text.randomColor());
					this.getText().setText("");
				}
			};
		}).setBounds(0, 500, 400, 200));
		i.getTextComp().getText().setSize(0.006f);
		frame.add(new JGLButton("B") {
			protected void onActionEvent() {
				bold = !bold;
				i.getText().setSelectedText(new String(new int[] {Formatting.FONT_CHANGE, (italic ? 1 : 0)+(bold ? 2 : 0)}, 0, 2));
			};
		}.setBounds(400, 600, 100, 100));
		frame.add(new JGLButton("I") {
			protected void onActionEvent() {
				italic = !italic;
				i.getText().setSelectedText(new String(new int[] {Formatting.FONT_CHANGE, (italic ? 1 : 0)+(bold ? 2 : 0)}, 0, 2));
			};
		}.setBounds(400, 500, 100, 100));
		frame.setVisible(true);
		while(!frame.getWindow().WindowShouldClose())
			if(Tools.every(1.0/60, 416146l))
				frame.repaint();
		frame.free();
	}

}
