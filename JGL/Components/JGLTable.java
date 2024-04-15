package Kartoffel.Licht.JGL.Components;

import Kartoffel.Licht.JGL.JGLComponent;

public class JGLTable extends JGLComponent{

	public JGLTable() {
		super(0, 0, 0, 0);
	}
	
	final public void transform(int offX, int offY) {
		for(JGLComponent c : components) {
			c.getBounds().x += offX;
			c.getBounds().y += offY;
			c.pack();
		}
	}

	public static void free() {
		// TODO Auto-generated method stub
		
	}

}
