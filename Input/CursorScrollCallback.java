package Kartoffel.Licht.Input;

import org.lwjgl.glfw.GLFWScrollCallback;

public class CursorScrollCallback extends GLFWScrollCallback implements InputCall{

	public double X = 0, Y = 0;
	public double MX = 0, MY = 0;
	public double xoffset, yoffset;
	
	@Override
	public void invoke(long window, double xoffset, double yoffset) {
		this.xoffset += xoffset;
		this.yoffset += yoffset;
	}

	@Override
	public void update() {
		MX = X-xoffset;
		MY = Y-yoffset;
		X = xoffset;
		Y = yoffset;
	}

}
