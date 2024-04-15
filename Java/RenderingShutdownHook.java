package Kartoffel.Licht.Java;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import Kartoffel.Licht.Rendering.GraphicWindow;

public class RenderingShutdownHook extends Thread{
	
	public static Thread shutdownHook = new RenderingShutdownHook();
	static boolean init = false;

	public static List<freeable> freeables = new ArrayList<freeable>();
	
	public static void init (){
		if(!init) {
			java.lang.Runtime.getRuntime().addShutdownHook(shutdownHook);
			init = true;
		}
		
	}
	
	@Override
	public void run() {
		GraphicWindow.unloadOpenGL();
		GLFW.glfwTerminate();
		for(int i = 0; i < freeables.size(); i++) {
			freeable f = freeables.get(i);
			try {
				f.free();
			}
			catch(Throwable e) {
				System.err.println("Failed to free Object '" + f + "' !");
				e.printStackTrace();;
			}
		}
	}

}
