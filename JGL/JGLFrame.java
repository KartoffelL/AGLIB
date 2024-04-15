package Kartoffel.Licht.JGL;

import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBlendFunc;

import java.util.List;

import org.lwjgl.opengl.GL33;

import Kartoffel.Licht.Input.KeyInputCallback;
import Kartoffel.Licht.Java.BufferedImage;
import Kartoffel.Licht.Java.Rectangle;
import Kartoffel.Licht.Java.freeable;
import Kartoffel.Licht.Rendering.GraphicWindow;
import Kartoffel.Licht.Tools.Timer;

/**
 * A JGLFrame is similar to the Swing variant, but with OpenGL.
 *
 */
public class JGLFrame implements freeable{
	
	public static final int JGL_HINT_UNRESIZABLE = 1000;
	public static final int JGL_HINT_UNDECORATED = 1001;
	
	
	public static final int EXIT_ON_CLOSE = 0,
							DISPOSE_ON_CLOSE = 1,
							HIDE_ON_CLOSE = 2;
	
	protected volatile GraphicWindow window;
	protected volatile boolean isWindow = true;
	
	volatile protected int closeOp = DISPOSE_ON_CLOSE;
	volatile protected boolean fullscreen = false;
	volatile protected boolean allowFT = true;
	
	volatile protected List<JGLComponent> components;
	
	volatile protected JGLManager manager;

	public JGLFrame() {
		NJGLFrame(true, 0, 0, 1, 1, "", 0);
	}
	public JGLFrame(String title) {
		NJGLFrame(true, 0, 0, 1, 1, title, 0);
	}
	public JGLFrame(int...hints) {
		for(int h : hints)
			if(h == JGL_HINT_UNDECORATED)
				 GraphicWindow.GLFW_DECORATED = false;
			else if(h == JGL_HINT_UNRESIZABLE)
				 GraphicWindow.GLFW_RESIZABLE = false;
		NJGLFrame(true, 0, 0, 1, 1, "", 0);
		GraphicWindow.GLFW_DECORATED = true;
		GraphicWindow.GLFW_RESIZABLE = true;
	}
	public JGLFrame(String title, int...hints) {
		for(int h : hints)
			if(h == JGL_HINT_UNDECORATED)
				 GraphicWindow.GLFW_DECORATED = false;
			else if(h == JGL_HINT_UNRESIZABLE)
				 GraphicWindow.GLFW_RESIZABLE = false;
		NJGLFrame(true, 0, 0, 1, 1, title, 0);
		GraphicWindow.GLFW_DECORATED = true;
		GraphicWindow.GLFW_RESIZABLE = true;
	}
	public JGLFrame(boolean centered, int px, int py, int w, int h, String title, long parent) {
		NJGLFrame(centered, px, py, w, h, title, parent);
	}
	protected void NJGLFrame(boolean centered, int px, int py, int w, int h, String title, long parent) {
		window = new GraphicWindow(centered, px, py, w, h, title, parent) {
			
			@Override
			public void WindowCloseCall() {
				if(closeOp == EXIT_ON_CLOSE) {
					try {free();}catch(Exception e) {e.printStackTrace();} //Safe Dispose
					System.exit(0);
				} else if(closeOp == DISPOSE_ON_CLOSE) {
					free();
				} else if(closeOp == HIDE_ON_CLOSE) {
					this.setVisible(false);
				}
			}
			
			@Override
			public void KeyCall(KeyInputCallback call) {
				if(allowFT)
					if(window.getCallback_Key().isKeyDown("F11"))
						window.toggleFullscreen();
			}
			
			
		};
		manager = new JGLManager(this);
		components = manager.createUI("default");
		allowFT = GraphicWindow.GLFW_RESIZABLE;
	}
	
	
	JGLFI info = new JGLFI();
	JGLFI getInfo() {
		info.hit = false;
		info.sx = window.getWidth();
		info.sy = window.getHeight();
		info.mx = window.getCallback_Cursor().getX();
		info.my = window.getCallback_Cursor().getY();
		info.window = window;
		info.frame = this;
		info.cursor = -1;
		return info;
	}
	
	public JGLFrame(GraphicWindow window) {
		isWindow = false;
		this.window = window;
		manager = new JGLManager(this);
		components = manager.createUI("default");
	}
	
	protected long oldTime = Timer.getTime();
	protected double Time = 0;
	
	public double repaint() {
		GL33.glViewport(0, 0, window.getWidth(), window.getHeight());
		GL33.glDepthFunc(GL33.GL_ALWAYS);
		if(isWindow) {
			window.updateWindow();
			GL33.glClear(GL33.GL_COLOR_BUFFER_BIT | GL33.GL_DEPTH_BUFFER_BIT | GL33.GL_STENCIL_BUFFER_BIT);
		}
		getInfo();
		GL33.glBlendFuncSeparate(GL_SRC_ALPHA, GL33.GL_ONE_MINUS_SRC_ALPHA, GL_SRC_ALPHA, GL33.GL_ONE);
		for(JGLComponent c : components) {
			c.paint(0, 0, info, true);
		}
		glBlendFunc(GL_SRC_ALPHA, GL33.GL_ONE_MINUS_SRC_ALPHA);
		if(!window.getCallback_Cursor().isGaming())
			window.getCallback_Cursor().setCursor(info.cursor);
	
		GL33.glDepthFunc(GL33.GL_LESS);
		
		//Generating Time in seconds, setting oldTime and returning.
		Time = (Timer.getTime()-oldTime)/1000000000.0;
		oldTime = Timer.getTime();
		return Time;
	}
	public double getTime() {
		return Time;
	}
	
	public JGLComponent add(JGLComponent c) {
		List<JGLComponent> comps = manager.getUIList("default");
		if(!comps.contains(c))
			comps.add(c);
		return c;
	}
	
	public List<JGLComponent> getComponents() {
		return components;
	}
	public JGLManager getManager() {
		return manager;
	}
	public void setComponents(List<JGLComponent> components) {
		this.components = components;
	}
	
	public void setDefaultCloseOperation(int c) {
		closeOp = c;
	}
	
	public void setIcon(BufferedImage image) {
		window.setIcon(image);
	}
	
	public void setVisible(boolean b) {
		window.setVisible(b);
	}
	
	public void setTitle(String t) {
		window.setWindowTitle(t);
	}
	
	public void setBounds(Rectangle r) {
		setBounds(r.x, r.y, r.width, r.height);
	}
	
	public void setBounds(int x, int y, int width, int height) {
		setPosition(x, y);
		setSize(width, height);
	}
	public Rectangle getBounds() {
		return new Rectangle(window.getPositionX(), window.getPositionY(), window.getWidth(), window.getHeight());
	}
	
	
	public void setSize(int width, int height) {
		window.setWidth(width);
		window.setHeight(height);
		window.applySize();
	}
	
	public void setPosition(int x, int y) {
		window.setPositionX(x);
		window.setPositionY(y);
		window.applyPosition();
	}
	
	public boolean isFullscreen() {
		return fullscreen;
	}
	
	public void setSizeBounds(int minX, int minY, int maxX, int maxY) {
		window.setMMSize(minX, minY, maxX, maxY);
	}
	
	public void swapComponents(List<JGLComponent> components) {
		JGLComponent[] c = (JGLComponent[]) components.toArray(new JGLComponent[components.size()]);
		components.clear();
		components.addAll(this.components);
		this.components.clear();
		for(JGLComponent d : c)
			this.components.add(d);
	}
	
	//-----------------
	
	
	public GraphicWindow getWindow() {
		return window;
	}
	
	public boolean isHovered() {
		return info.hit;
	}
	public void setAllowFullscreen(boolean allowFT) {
		this.allowFT = allowFT;
	}
	public boolean isAllowFullscreen() {
		return allowFT;
	}
	@Override
	public void free() {
		JGLComponent.freeAllComponents();
		if(isWindow)
			window.free();
		manager.free();
	}
	

}
