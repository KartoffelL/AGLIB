package Kartoffel.Licht.JGL;

import org.lwjgl.glfw.GLFWWindowRefreshCallback;

import Kartoffel.Licht.JGL.Components.JGLBackground;
import Kartoffel.Licht.JGL.Components.JGLSquare;
import Kartoffel.Licht.JGL.Components.JGLTextField;
import Kartoffel.Licht.JGL.Components.JGLTextInput;
import Kartoffel.Licht.Java.Color;
import Kartoffel.Licht.Rendering.GraphicWindow;
import Kartoffel.Licht.Rendering.Text.GlobalFont;
import Kartoffel.Licht.Tools.ImGuiTools;

public class test {
	
	public static void main(String[] args) {
		GraphicWindow.GLFW_FLOATING = true;
		JGLFrame frame = new JGLFrame();
		frame.setSize(500, 500);
		frame.getWindow().listeners.add(new GLFWWindowRefreshCallback() {
			
			@Override
			public void invoke(long window) {
				frame.repaint();
			}
		});
		frame.setVisible(true);
		GlobalFont.init();
		
		frame.add(new JGLBackground(Color.WHITE));
		frame.add(new JGLSquare(Color.GREEN).setBounds(-50, -50, 100, 100).setBindings(0, 0));
		frame.add(new JGLTextField("hello").setBounds(-50, -50, 100, 100).setBindings(0, 0));
		JGLTextInput ti = new JGLTextInput();
		frame.add(ti.setBindings(-1, -1).setBounds(0, 0, 250, 50));
		ImGuiTools.ImGui_Init(frame.getWindow());
		
		while(!frame.getWindow().WindowShouldClose()) {
			ImGuiTools.ImGui_startDraw();
			
			
			ImGuiTools.ImGui_endDraw();
			
			GraphicWindow.doPollEvents();
			frame.repaint();
		}
	}
}
