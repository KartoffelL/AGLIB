package Kartoffel.Licht.JGL.Components;

import Kartoffel.Licht.Input.CursorScrollCallback;
import Kartoffel.Licht.JGL.JGLComponent;
import Kartoffel.Licht.JGL.JGLFI;

public class JGLScrollPane extends JGLComponent{

	public JGLScrollPane() {
		super(0, 0, 0, 0);
	}
	
	private double sensitivity = 50;
	
	@Override
	protected void paintComponent(JGLFI info, int xoff, int yoff) {
		CursorScrollCallback call = info.window.getCallback_CursorScroll();
		move(0, (int) (-call.MY*sensitivity));
	}
	
	public double getSensitivity() {
		return sensitivity;
	}
	public void setSensitivity(double sensitivity) {
		this.sensitivity = sensitivity;
	}

}
