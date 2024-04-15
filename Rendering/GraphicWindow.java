package Kartoffel.Licht.Rendering;

import static Kartoffel.Licht.Tools.Tools.err;
import static Kartoffel.Licht.Tools.Tools.log;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwSetWindowSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowTitle;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glEnable;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCharCallbackI;
import org.lwjgl.glfw.GLFWCursorEnterCallbackI;
import org.lwjgl.glfw.GLFWCursorPosCallbackI;
import org.lwjgl.glfw.GLFWDropCallbackI;
import org.lwjgl.glfw.GLFWErrorCallbackI;
import org.lwjgl.glfw.GLFWFramebufferSizeCallbackI;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWJoystickCallbackI;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.glfw.GLFWMonitorCallbackI;
import org.lwjgl.glfw.GLFWScrollCallbackI;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowCloseCallbackI;
import org.lwjgl.glfw.GLFWWindowContentScaleCallbackI;
import org.lwjgl.glfw.GLFWWindowFocusCallbackI;
import org.lwjgl.glfw.GLFWWindowIconifyCallbackI;
import org.lwjgl.glfw.GLFWWindowMaximizeCallbackI;
import org.lwjgl.glfw.GLFWWindowPosCallbackI;
import org.lwjgl.glfw.GLFWWindowRefreshCallbackI;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL33;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.system.Callback;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import Kartoffel.Licht.Input.CursorCallback;
import Kartoffel.Licht.Input.CursorScrollCallback;
import Kartoffel.Licht.Input.JoystickInputCallback;
import Kartoffel.Licht.Input.KeyInputCallback;
import Kartoffel.Licht.Input.TextInputCallback;
import Kartoffel.Licht.Java.BufferedImage;
import Kartoffel.Licht.Java.Rectangle;
import Kartoffel.Licht.Java.RenderingShutdownHook;
import Kartoffel.Licht.Java.freeable;
import Kartoffel.Licht.Java.namable;

/**
 * A GraphicWindow just creates a Window and handles some openGL stuff.
 *
 */
public class GraphicWindow implements namable, freeable{
	
	public long WINDOW_ID;
	
	public FrameBuffer CURRENT_FRAMEBUFFER = null;
	
	public static boolean openGLloaded = true;
	public static Thread mainThread = null;
	public GLCapabilities localCapabilities = null;
	static record action(GraphicWindow window, String action, Object[] args, action.result r) {
		public synchronized void put(Object res) {r.res = res;}static class result {Object res = null;volatile boolean f = false;}};
	protected static List<GraphicWindow> windows = new ArrayList<GraphicWindow>();
	protected List<action> pending = new CopyOnWriteArrayList<GraphicWindow.action>();
	public Object addPending(String function, Object...args) {
		action a = new action(this, function, args, new action.result());
		pending.add(a);
		synchronized (a) {
			try {
				a.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		a.r.f = true;
		return a.r.res;
	}
	public static boolean hasWindows() {
		return windows.size() != 0;
	}
	
	
	
	public void bindFrameBuffer(FrameBuffer fb) {
		if(fb != null)
			fb.bind();
		else {
			GL11.glViewport(0, 0, getWidth(), getHeight());
			GL33.glBindFramebuffer(GL33.GL_FRAMEBUFFER, 0);
		}
		CURRENT_FRAMEBUFFER = fb;
	}
	
	private String name = "Window";
	
	public static final int MULTI_SAMPLE = 4;
	
	 public static boolean
	     GLFW_FOCUSED                 = false,
	     GLFW_ICONIFIED               = false,
	     GLFW_RESIZABLE               = true,
	     GLFW_VISIBLE                 = false,
	     GLFW_DECORATED               = true,
	     GLFW_AUTO_ICONIFY            = false,
	     GLFW_FLOATING                = false,
	     GLFW_MAXIMIZED               = false,
	     GLFW_CENTER_CURSOR           = false,
	     GLFW_TRANSPARENT_FRAMEBUFFER = true,
	     GLFW_HOVERED                 = false,
	     GLFW_FOCUS_ON_SHOW           = true,
	     GLFW_MOUSE_PASSTHROUGH       = false;
	
	protected CursorCallback cursor;
	protected CursorScrollCallback cursor_scroll;
	protected JoystickInputCallback joystick;
	protected KeyInputCallback key;
	protected TextInputCallback text;
	
	final public List<Callback> listeners = new ArrayList<>();
	
	public long PARENT = 0;
	
	//###################################Window Variables
	
	protected Rectangle bounds;
	protected String title;
	protected boolean focused;
	protected boolean windowed_fullscreen = true;
	protected boolean windwed_fullscreen_workspace = false;
	
	public GraphicWindow() {
		
	}
	/**
	 * Creates a new 1000x1000 window with the given title
	 * @param title the title of the window
	 */
	public GraphicWindow(String title) {
		this.init(true, 0, 0, 1000, 1000, title);
	}
	/**
	 * Creates a new window
	 * @param centered
	 * @param px
	 * @param py
	 * @param w
	 * @param h
	 * @param title
	 */
	public GraphicWindow(boolean centered, int px, int py, int w, int h, String title) {
		this.init(centered, px, py, w, h, title);
	}
	/**
	 * Creates a new window
	 * @param centered if the window should be centered
	 * @param px the X position of the window, ignored if window is centered
	 * @param py the Y position of the window, ignored if window is centered
	 * @param w the width of the window
	 * @param h the height of the window
	 * @param title the title of the window
	 * @param parent the parent of the window
	 */
	public GraphicWindow(boolean centered, int px, int py, int w, int h, String title, long parent) {
		this.PARENT = parent;
		this.init(centered, px, py, w, h, title);
	}
	
	/**
	 * Initializes the Window.
	 * @return
	 */
	public boolean init(boolean centered, int px, int py, int w, int h, String title) {
		
		this.bounds = new Rectangle(px, py, w, h);
		//Window
		if(!glfwInit()) {
			err("Could not initialize GLFW!");
			return false;
		}
		mainThread = Thread.currentThread();
		windows.add(this);
		loadOpenGL();
		
		//Hints
		
		GLFW.glfwWindowHint(GLFW.GLFW_FOCUSED, GLFW_FOCUSED ? 1 : 0);
		GLFW.glfwWindowHint(GLFW.GLFW_ICONIFIED,  GLFW_ICONIFIED ? 1 : 0);
		GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE,  GLFW_RESIZABLE ? 1 : 0);
		GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE,  GLFW_VISIBLE ? 1 : 0);
		GLFW.glfwWindowHint(GLFW.GLFW_DECORATED,  GLFW_DECORATED ? 1 : 0);
		GLFW.glfwWindowHint(GLFW.GLFW_AUTO_ICONIFY,  GLFW_AUTO_ICONIFY ? 1 : 0);
		GLFW.glfwWindowHint(GLFW.GLFW_FLOATING,  GLFW_FLOATING ? 1 : 0);
		GLFW.glfwWindowHint(GLFW.GLFW_MAXIMIZED,  GLFW_MAXIMIZED ? 1 : 0);
		GLFW.glfwWindowHint(GLFW.GLFW_CENTER_CURSOR, GLFW_CENTER_CURSOR ? 1 : 0);
		GLFW.glfwWindowHint(GLFW.GLFW_TRANSPARENT_FRAMEBUFFER, GLFW_TRANSPARENT_FRAMEBUFFER ? 1 : 0);
		GLFW.glfwWindowHint(GLFW.GLFW_HOVERED, GLFW_HOVERED ? 1 : 0);
		GLFW.glfwWindowHint(GLFW.GLFW_FOCUS_ON_SHOW, GLFW_FOCUS_ON_SHOW ? 1 : 0);
		GLFW.glfwWindowHint(GLFW.GLFW_MOUSE_PASSTHROUGH, GLFW_MOUSE_PASSTHROUGH ? 1 : 0);
		
		WINDOW_ID = glfwCreateWindow(w, h, title, 0, PARENT);
		if(WINDOW_ID == 0) {
			err("Failed to create Window!");
			PointerBuffer b = PointerBuffer.allocateDirect(1);
			GLFW.glfwGetError(b);
			err("Error: " + b.get());
			return false;
		}
		//Variables
		cursor = new CursorCallback(this);
		cursor_scroll = new CursorScrollCallback();
		joystick = new JoystickInputCallback();
		text = new TextInputCallback();
		key = new KeyInputCallback(text, WINDOW_ID);
		
		GLFWVidMode videoMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		if(centered)
			glfwSetWindowPos(WINDOW_ID, (videoMode.width()-w)/2, (videoMode.height()-h)/2);
		else
			glfwSetWindowPos(WINDOW_ID, px, py);
		//OpenGL stuff
		bindGLFW();
		createOpenGL();
		
		//GLFW INIT STUFF
		cursor.createCursors();
		setCallbacks();
		
		log("Succesfully generated Window with ID; " + WINDOW_ID);
		return true;
	}
	protected void setCallbacks() {
		//######################################Main_Inputs##############################
		
				GLFW.glfwSetKeyCallback(WINDOW_ID, new GLFWKeyCallbackI() {
					
					@Override
					public void invoke(long window, int keyy, int scancode, int action, int mods) {
						key.invoke(window, keyy, scancode, action, mods);
						KeyCall(key);
						for(Callback l : listeners)if(l instanceof GLFWKeyCallbackI)((GLFWKeyCallbackI)l).invoke(window, keyy, scancode, action, mods);
					}
				});
				GLFW.glfwSetJoystickCallback(new GLFWJoystickCallbackI() {
					
					@Override
					public void invoke(int jid, int event) {
						joystick.invoke(jid, event);
						JoystickCall(joystick);
						for(Callback l : listeners)if(l instanceof GLFWJoystickCallbackI)((GLFWJoystickCallbackI)l).invoke(jid, event);
					}
				});
				GLFW.glfwSetCursorPosCallback(WINDOW_ID, new GLFWCursorPosCallbackI() {
					
					@Override
					public void invoke(long window, double xpos, double ypos) {
						cursor.invoke(window, xpos, ypos);
						CursorCall(cursor);
						for(Callback l : listeners)if(l instanceof GLFWCursorPosCallbackI)((GLFWCursorPosCallbackI)l).invoke(window, xpos, ypos);
					}
				});
				GLFW.glfwSetCharCallback(WINDOW_ID, new GLFWCharCallbackI() {
					
					@Override
					public void invoke(long window, int codepoint) {
						text.invoke(window, codepoint);
						CharCall(text);
						for(Callback l : listeners)if(l instanceof GLFWCharCallbackI)((GLFWCharCallbackI)l).invoke(window, codepoint);
					}
				});
				GLFW.glfwSetScrollCallback(WINDOW_ID, new GLFWScrollCallbackI() {
					
					@Override
					public void invoke(long window, double xoffset, double yoffset) {
						cursor_scroll.invoke(window, xoffset, yoffset);
						ScrollCall(cursor_scroll);
						for(Callback l : listeners)if(l instanceof GLFWScrollCallbackI)((GLFWScrollCallbackI)l).invoke(window, xoffset, yoffset);
					}
				});
				
				//###################################################################
				 {
					glfwSetWindowSizeCallback(WINDOW_ID, new GLFWWindowSizeCallback() {
				        @Override
				        public void invoke(long window, int argWidth, int argHeight) {
				        	bounds.width = argWidth;
				        	bounds.height = argHeight;
				        	WindowSizeCall(argWidth, argHeight);
							for(Callback l : listeners)if(l instanceof GLFWWindowSizeCallback)((GLFWWindowSizeCallback)l).invoke(window, argWidth, argHeight);
				        }
					});
					GLFW.glfwSetWindowPosCallback(WINDOW_ID, new GLFWWindowPosCallbackI() {
						
						@Override
						public void invoke(long window, int xpos, int ypos) {
							bounds.x = xpos;
							bounds.y = ypos;
							WindowPosCall(xpos, ypos);
							for(Callback l : listeners)if(l instanceof GLFWWindowPosCallbackI)((GLFWWindowPosCallbackI)l).invoke(window, xpos, ypos);
						}
					});
					GLFW.glfwSetWindowRefreshCallback(WINDOW_ID, new GLFWWindowRefreshCallbackI() {
						
						@Override
						public void invoke(long window) {
							doWindowPollEvents();
							WindowRefreshCall();
							for(Callback l : listeners)if(l instanceof GLFWWindowRefreshCallbackI)((GLFWWindowRefreshCallbackI)l).invoke(window);
						}
					});
					GLFW.glfwSetWindowMaximizeCallback(WINDOW_ID, new GLFWWindowMaximizeCallbackI() {
						
						@Override
						public void invoke(long window, boolean maximized) {
							MaximizeCall(maximized);
							for(Callback l : listeners)if(l instanceof GLFWWindowMaximizeCallbackI)((GLFWWindowMaximizeCallbackI)l).invoke(window, maximized);
						}
					});
					GLFW.glfwSetWindowIconifyCallback(WINDOW_ID, new GLFWWindowIconifyCallbackI() {
						
						@Override
						public void invoke(long window, boolean iconified) {
							IconifyCall(iconified);
							for(Callback l : listeners)if(l instanceof GLFWWindowIconifyCallbackI)((GLFWWindowIconifyCallbackI)l).invoke(window, iconified);
						}
					});
					GLFW.glfwSetWindowFocusCallback(WINDOW_ID, new GLFWWindowFocusCallbackI() {
						
						@Override
						public void invoke(long window, boolean focused) {
							GraphicWindow.this.focused = focused;
							WindowFocusCall(focused);
							for(Callback l : listeners)if(l instanceof GLFWWindowFocusCallbackI)((GLFWWindowFocusCallbackI)l).invoke(window, focused);
						}
					});
					GLFW.glfwSetWindowContentScaleCallback(WINDOW_ID, new GLFWWindowContentScaleCallbackI() {
						
						@Override
						public void invoke(long window, float xscale, float yscale) {
							ContentScaleCall(xscale, yscale);
							for(Callback l : listeners)if(l instanceof GLFWWindowContentScaleCallbackI)((GLFWWindowContentScaleCallbackI)l).invoke(window, xscale, yscale);
						}
					});
					GLFW.glfwSetWindowCloseCallback(WINDOW_ID, new GLFWWindowCloseCallbackI() {
						
						@Override
						public void invoke(long window) {
							WindowCloseCall();
							for(Callback l : listeners)if(l instanceof GLFWWindowCloseCallbackI)((GLFWWindowCloseCallbackI)l).invoke(window);
						}
					});
					GLFW.glfwSetMonitorCallback(new GLFWMonitorCallbackI() {
						
						@Override
						public void invoke(long monitor, int event) {
							MonitorCall(monitor);
							for(Callback l : listeners)if(l instanceof GLFWMonitorCallbackI)((GLFWMonitorCallbackI)l).invoke(monitor, event);
						}
					});
					GLFW.glfwSetFramebufferSizeCallback(WINDOW_ID, new GLFWFramebufferSizeCallbackI() {
						
						@Override
						public void invoke(long window, int width, int height) {
							FrameBufferCall(width, height);
							for(Callback l : listeners)if(l instanceof GLFWFramebufferSizeCallbackI)((GLFWFramebufferSizeCallbackI)l).invoke(window, width, height);
						}
					});
					GLFW.glfwSetErrorCallback(new GLFWErrorCallbackI() {
						
						@Override
						public void invoke(int error, long description) {
							ErrorCall(error, description);
							for(Callback l : listeners)if(l instanceof GLFWErrorCallbackI)((GLFWErrorCallbackI)l).invoke(error, description);
						}
					});
					GLFW.glfwSetCursorEnterCallback(WINDOW_ID, new GLFWCursorEnterCallbackI() {
						
						@Override
						public void invoke(long window, boolean entered) {
							CursorEnterCall(entered);
							for(Callback l : listeners)if(l instanceof GLFWCursorEnterCallbackI)((GLFWCursorEnterCallbackI)l).invoke(window, entered);
						}
					});
					GLFW.glfwSetDropCallback(WINDOW_ID, new GLFWDropCallbackI() {
						
						@Override
						public void invoke(long window, int count, long names) {
						    PointerBuffer nameBuffer = MemoryUtil.memPointerBuffer(names, count);
						    String[] s = new String[count];
						    for ( int i = 0; i < count; i++ ) {
						    	ByteBuffer b = MemoryUtil.memByteBufferNT1(nameBuffer.get(i));
						        s[i] = MemoryUtil.memUTF8(b);
						    }
							FileDropCall(s);
							for(Callback l : listeners)if(l instanceof GLFWDropCallbackI)((GLFWDropCallbackI)l).invoke(window, count, names);
						}
					});
				}
	}
	/**
	 * Creates openGL capabilities
	 */
	public GLCapabilities createOpenGL() {
		localCapabilities = GL.createCapabilities();
		GL33.glHint(GL33.GL_TEXTURE_COMPRESSION_HINT, GL33.GL_NICEST);
		
		GL33.glLineWidth(5);
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_CULL_FACE); 
		glEnable(GL_TEXTURE_2D);
		GL13.glEnable(GL13.GL_MULTISAMPLE);
		GL13.glEnable(GL13.GL_BLEND);
		GL13.glEnable(GL33.GL_VERTEX_PROGRAM_POINT_SIZE);
		GL33.glEnable(GL33.GL_CLIP_DISTANCE0);
		GL33.glEnable(GL33.GL_CLIP_DISTANCE1);
		GL33.glEnable(GL33.GL_CLIP_DISTANCE2);
		GL33.glEnable(GL33.GL_CLIP_DISTANCE3);
		GL33.glBlendFuncSeparate(GL33.GL_SRC_ALPHA, GL33.GL_ONE_MINUS_SRC_ALPHA, GL33.GL_ONE, GL33.GL_ZERO);
		GL33.glPixelStorei(GL33.GL_UNPACK_ALIGNMENT, 1);
		
		glClearColor(0, 0, 0, 0);
		
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		return localCapabilities;
	}
	public void loadCapOnThread() {
		GL.setCapabilities(localCapabilities);
		bindGLFW();
	}
	public void unloadCapOnThread() {
		GL.setCapabilities(null);
		glfwMakeContextCurrent(0);
	}
	/**
	 * Unloads OpenGL
	 */
	public static void unloadOpenGL() {
		if(openGLloaded) {
			GL.destroy();
			openGLloaded = false;
		}
	}
	public static void loadOpenGL() {
		RenderingShutdownHook.init();
		if(!openGLloaded) {
			GL.create();
			openGLloaded = true;
		}
	}
	/**
	 * Binds GLFW
	 */
	public void bindGLFW() {
		glfwMakeContextCurrent(WINDOW_ID);
		GLFW.glfwSwapInterval(0); //Disables VSync
	}
	
	/**
	 * Swaps the buffers of the window and clears the (then) back buffer. Also updates Input.
	 */
	public void updateWindow(boolean clear) {
		glfwSwapBuffers(WINDOW_ID); 
		if(clear) {
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		}
		cursor.update();
		cursor_scroll.update();
		text.update();
		joystick.update();
		key.update();
	}
	/**
	 * Swaps the buffers of the window. (Does not clear back buffer)
	 */
	public final void updateWindow() {
		updateWindow(false);
	}
	
	private boolean[] wasClick = new boolean[3];
	/**
	 * Updates the window. This function should be called every frame
	 */
	public static final boolean doPollEvents() {
		if(Thread.currentThread() != mainThread)
			return false;
		glfwPollEvents();
		doWindowPollEvents();
		return true;
	}
	
	protected static final boolean doWindowPollEvents() {
		if(Thread.currentThread() != mainThread)
			throw new RuntimeException("Fatal check!");
		for(int k = 0; k < windows.size(); k++) {
			try {
				GraphicWindow gw = windows.get(k);
				if(gw != null) {
					for(int i = 0; i < 3; i++) {
						if(gw.key.isMouseButtonDown(i) != gw.wasClick[i]) {
							gw.wasClick[i] = gw.key.isMouseButtonDown(i);
							gw.CursorClickCall(gw.wasClick[i], i);
						}
					}
					for(int i = 0; i < gw.pending.size(); i++) {
						action a = gw.pending.get(0);
						switch (a.action) {
							case "free":
								a.window.free();
								break;
							case "WindowShouldClose":
								a.put(a.window.WindowShouldClose());
								break;
							case "resetWindowShouldClose":
								a.window.resetWindowShouldClose();
								break;
							case "takeScreenshot":
								if(a.args.length == 0)
									a.put(a.window.takeScreenshot());
								else
									a.put(a.window.takeScreenshot((FrameBuffer)a.args[0], (int)a.args[1]));
								break;
							case "takeScreenshotBytes ":
								if(a.args.length == 1)
									a.put(a.window.takeScreenshotBytes((int) a.args[0]));
								else
									a.put(a.window.takeScreenshotBytes((FrameBuffer)a.args[0], (int)a.args[1], (int)a.args[2]));
								break;
							case "getUserAttention":
								a.put(a.window.getUserAttention());
								break;
							case "applyPosition":
								a.put(a.window.applyPosition());
								break;
							case "applySize":
								a.put(a.window.applySize());
								break;
							case "setWindowTitle":
								a.put(a.window.setWindowTitle((String) a.args[0]));
								break;
							case "setVisible":
								a.put(a.window.setVisible((boolean) a.args[0]));
								break;
							case "setIcon":
								if(a.args.length == 2)
									a.put(a.window.setIcon((BufferedImage)a.args[0], (int) a.args[1]));
								else
									a.put(a.window.setIcon((GLFWImage.Buffer)a.args[0]));
								break;
							case "setFullscreen":
								a.put(a.window.setFullscreen((boolean) a.args[0]));
								break;
							case "setMMSize":
								a.put(a.window.setMMSize((int)a.args[0], (int)a.args[1], (int)a.args[2], (int)a.args[3]));
							case "setAspectRatio":
								a.put(a.window.setAspectRatio((int)a.args[0], (int)a.args[1]));
								break;
							default:
								throw new IllegalArgumentException("Unsupported value: " + a.action);
						}
						synchronized (a) {
							a.notifyAll();
						}
						if(gw.pending.get(0).r.f)
							gw.pending.remove(0);
					}
				}
			} catch (Exception e) {e.printStackTrace();}
			
		}
		return true;
	}
	
	public static final boolean terminateGLFW() {
		if(Thread.currentThread() != mainThread)
			return false;
		glfwTerminate();
		log("Terminated GLFW!");
		return true;
	}
	
	public static final void freeStatic() {
		for(int i = 0; i < windows.size(); i++)
			windows.get(i).requestClose();
		if(Thread.currentThread() == mainThread) {
			while(windows.size() != 0)
				doWindowPollEvents();
		}
	}
	
	public static final List<GraphicWindow> getActive() {
		return windows;
	}
	
	public void ScrollCall(CursorScrollCallback call) {}
	public void CharCall(TextInputCallback call) {}
	public void CursorCall(CursorCallback call) {}
	public void JoystickCall(JoystickInputCallback call) {}
	public void KeyCall(KeyInputCallback call) {}
	public void FileDropCall(String[] paths) {}
	public void CursorEnterCall(boolean entered) {}
	public void CursorClickCall(boolean press, int key) {}
	public void ErrorCall(int error, long description) {}
	public void FrameBufferCall(int width, int height) {}
	public void MonitorCall(long monitor) {}
	public void WindowCloseCall() {}
	public void ContentScaleCall(float xscale, float yscale) {}
	public void WindowFocusCall(boolean focused) {}
	public void IconifyCall(boolean iconify) {}
	public void WindowRefreshCall() {}
	public void MaximizeCall(boolean maximized) {}
	/**
	 * Is run, if the Window size is changed
	 */
	public void WindowSizeCall(int sx, int sy) {}
	
	/**
	 * Is run, if the Window position is changed
	 */
	public void WindowPosCall(int px, int py) {}
	/**
	 * Frees all Resources held
	 */
	@Override
	public void free() {
		if(Thread.currentThread() != mainThread) {
			addPending("free");
			return;
		}
		GLFW.glfwDestroyWindow(WINDOW_ID);
		windows.remove(this);
	}
	
	
	boolean requestClose = false;
	/**
	 * Returns if the Window should close
	 * @return
	 */
	public final boolean WindowShouldClose() {
		if(requestClose)
			return true;
		if(Thread.currentThread() != mainThread) {
			return (boolean) addPending("WindowShouldClose");
		}
		boolean res = glfwWindowShouldClose(WINDOW_ID);
		return res || requestClose;
	}
	public final void resetWindowShouldClose() {
		if(Thread.currentThread() != mainThread) {
			addPending("resetWindowShouldClose");
			return;
		}
		GLFW.glfwSetWindowShouldClose(WINDOW_ID, false);
	}
	
	/**
	 * Requests the window to close
	 */
	public final void requestClose() {
		requestClose = true;
	}
	/**
	 * Takes a screenshot of the window
	 * @return
	 */
	public BufferedImage takeScreenshot() {
		if(Thread.currentThread() != mainThread) {
			return (BufferedImage) addPending("takeScreenshot");
		}
		ByteBuffer bb = MemoryUtil.memAlloc(getWidth()*getHeight()*4);
		bindFrameBuffer(null);
		GL33.glReadBuffer(GL33.GL_BACK);
		GL33.glReadPixels(0, 0, getWidth(), getHeight(), GL33.GL_RGBA, GL33.GL_UNSIGNED_BYTE, bb);
		BufferedImage bi = new BufferedImage(bb, getWidth(), getHeight());
		MemoryUtil.memFree(bb);
		return bi;
	}
	/**
	 * Takes a screenshot of the FrameBuffer
	 * @param fbo
	 * @param colorAtt the color attachment of the FrameBuffer
	 * @return
	 */
	public BufferedImage takeScreenshot(FrameBuffer fbo, int colorAtt) {
		if(Thread.currentThread() != mainThread) {
			return (BufferedImage) addPending("takeScreenshot", fbo, colorAtt);
		}
		if(fbo == null)
			return takeScreenshot();
		int width = fbo.getWidth();
		int height = fbo.getHeight();
		ByteBuffer bb = MemoryUtil.memAlloc(width*height*4);
		fbo.bind();
		GL33.glReadBuffer(GL33.GL_COLOR_ATTACHMENT0+colorAtt);
		GL33.glReadPixels(0, 0, width, height, GL33.GL_RGBA, GL33.GL_UNSIGNED_BYTE, bb);
		BufferedImage bi = new BufferedImage(bb, width, height);
		 MemoryUtil.memFree(bb);
		 fbo.bindDefaultFrameBuffer();
		return bi;
	}
	
	/**
	 * Takes a screenshot of the window
	 * @return
	 */
	public ByteBuffer takeScreenshotBytes(int channels) {
		if(Thread.currentThread() != mainThread) {
			return (ByteBuffer) addPending("takeScreenshotBytes", channels);
		}
		ByteBuffer bb = MemoryUtil.memAlloc(getWidth()*getHeight()*channels);
		bindFrameBuffer(null);
		GL33.glReadBuffer(GL33.GL_BACK);
		int c = channels == 1 ? GL33.GL_RED : channels == 2 ? GL33.GL_RG : channels == 3 ? GL33.GL_RGB : GL_RGBA;
		GL33.glReadPixels(0, 0, getWidth(), getHeight(), c, GL33.GL_UNSIGNED_BYTE, bb);
		return bb;
	}
	/**
	 * Takes a screenshot of the FrameBuffer
	 * @param fbo
	 * @param colorAtt the color attachment of the FrameBuffer
	 * @return
	 */
	public ByteBuffer takeScreenshotBytes(FrameBuffer fbo, int colorAtt, int channels) {
		if(Thread.currentThread() != mainThread) {
			return (ByteBuffer) addPending("takeScreenshotBytes", fbo, colorAtt, channels);
		}
		if(fbo == null)
			return takeScreenshotBytes(channels);
		int width = fbo.getWidth();
		int height = fbo.getHeight();
		ByteBuffer bb = MemoryUtil.memAlloc(width*height*channels);
		fbo.bind();
		GL33.glReadBuffer(GL33.GL_COLOR_ATTACHMENT0+colorAtt);
		int c = channels == 1 ? GL33.GL_RED : channels == 2 ? GL33.GL_RG : channels == 3 ? GL33.GL_RGB : GL_RGBA;
		GL33.glReadPixels(0, 0, width, height, c, GL33.GL_UNSIGNED_BYTE, bb);
		return bb;
	}
	
	/**
	 * Binds the window to the cursor position.
	 * @return
	 */
	public GraphicWindow moveWindowWithMouse() {
		setPositionX(bounds.x-cursor.getRmotionX());
		setPositionY(bounds.y-cursor.getRmotionY());
		return this;
	}
	
	
	//############################################Bounds
	/**
	 * Returns the height of the window
	 * @return
	 */
	public final int getHeight() {
		return bounds.height;
	}
	/**
	 * Sets the height of the window
	 * @param h
	 * @return
	 */
	public final GraphicWindow setHeight(int h) {
		bounds.height = h;
		applySize();
		return this;
	}
	/**
	 * Returns the width of the window
	 * @return
	 */
	public final int getWidth() {
		return bounds.width;
	}
	/**
	 * Sets the width of the window
	 * @param w
	 * @return
	 */
	public final GraphicWindow setWidth(int w) {
		bounds.width = w;
		applySize();
		return this;
	}
	/**
	 * Returns the aspect ratio of the window
	 * @return
	 */
	public float getWindowAspectRatio() {
		return (float)bounds.width/bounds.height;
	}
	/**
	 * Requests the attention of the user
	 * @return
	 */
	public GraphicWindow getUserAttention() {
		if(Thread.currentThread() != mainThread) {
			addPending("getUserAttention");
			return this;
		}
		GLFW.glfwRequestWindowAttention(WINDOW_ID);
		return this;
	}
	/**
	 * Returns the Y-position of the window
	 * @return
	 */
	public final int getPositionY() {
		return bounds.y;
	}
	/**
	 * Sets the Y-position of the window
	 * @param w
	 * @return
	 */
	public final GraphicWindow setPositionY(int h) {
		bounds.y = h;
		applyPosition();
		return this;
	}
	/**
	 * Returns the X-position of the window
	 * @return
	 */
	public final int getPositionX() {
		return bounds.x;
	}
	/**
	 * Sets the X-position of the window
	 * @param w
	 * @return
	 */
	public final GraphicWindow setPositionX(int w) {
		bounds.x = w;
		applyPosition();
		return this;
	}
	/**
	 * Applies any changes made to the window position
	 */
	public final GraphicWindow applyPosition() {
		if(Thread.currentThread() != mainThread) {
			addPending("applyPosition");
			return this;
		}
		GLFW.glfwSetWindowPos(WINDOW_ID, bounds.x, bounds.y);
		return this;
	}
	/**
	 * Applies any changes made to the window size
	 */
	public final GraphicWindow applySize() {
		if(Thread.currentThread() != mainThread) {
			addPending("applySize");
			return this;
		}
		GLFW.glfwSetWindowSize(WINDOW_ID, bounds.width, bounds.height);
		return this;
	}
	
	
	//############################################Title
	/**
	 * Sets the Window Title
	 * @param window_Title
	 */
	public final GraphicWindow setWindowTitle(String title) {
		if(Thread.currentThread() != mainThread) {
			addPending("setWindowTitle", title);
			return this;
		}
		glfwSetWindowTitle(WINDOW_ID, title);
		this.title = title;
		return this;
	}
	/**
	 * Returns the Window Title
	 * @param window_Title
	 */
	public final String getWindowTitle() {
		return title;
	}
	//##########################################Window State
	/**
	 * Changes the window visibility
	 * @param b
	 */
	public final GraphicWindow setVisible(boolean b) {
		if(Thread.currentThread() != mainThread) {
			addPending("setVisible", b);
			return this;
		}
		if(b)
			glfwShowWindow(WINDOW_ID);
		else
			GLFW.glfwHideWindow(WINDOW_ID);
		return this;
	}
	/**
	 * Sets the window Icon
	 * @param image
	 */
	public final GraphicWindow setIcon(org.lwjgl.glfw.GLFWImage.Buffer image) {
		if(Thread.currentThread() != mainThread) {
			addPending("setIcon", image);
			return this;
		}
		GLFW.glfwSetWindowIcon(WINDOW_ID, image);
		return this;
	}
	/**
	 * Sets the window Icon
	 * @param image_b
	 */
	public final GraphicWindow setIcon(BufferedImage image_b) {
		setIcon(image_b, 48);
		return this;
	}
	/**
	 * Sets the window icon
	 * @param image_b
	 * @param size
	 */
	public final GraphicWindow setIcon(BufferedImage image_b, int size) {
		if(Thread.currentThread() != mainThread) {
			addPending("setIcon", image_b, size);
			return this;
		}
	    try (MemoryStack stack = MemoryStack.stackPush()){
	    	ByteBuffer b = stack.malloc(4*image_b.getWidth()*image_b.getHeight());
	    	b.put(0, image_b.toByteArray(4));
	    	GLFWImage.Buffer icons = new GLFWImage.Buffer(stack.malloc(16));
	    	GLFWImage glfwImage = new GLFWImage(stack.calloc(16));
	    	glfwImage.set(image_b.getWidth(), image_b.getHeight(), b);
	    	icons.put(0, glfwImage);
	        GLFW.glfwSetWindowIcon(WINDOW_ID, icons);
	    }
	    return this;
	}
	Rectangle origB = new Rectangle();
	volatile boolean fs = false;
	/**
	 * Sets the window to be fullscreen
	 * @param m
	 */
	public final GraphicWindow setFullscreen(boolean m) {
		if(Thread.currentThread() != mainThread) {
			addPending("setFullscreen", m);
			return this;
		}
		if(fs == m)
			return this;
		fs = m;
		long monitor = getCurrentMonitor();
		if(m) {
			origB.setBounds(bounds);
			if(windowed_fullscreen) {
				int[] px = new int[1];
				int[] py = new int[1];
				int[] pw = new int[1];
				int[] ph = new int[1];
//				GLFW.glfwGetMonitorPos(monitor, px, py);
//				GLFW.glfwSetWindowPos(WINDOW_ID, px[0], py[0]);
				GLFW.glfwGetMonitorWorkarea(monitor, px, py, pw, ph);
				if(!windwed_fullscreen_workspace) {
					GLFWVidMode vid = GLFW.glfwGetVideoMode(monitor);
					pw[0] = vid.width();
					ph[0] = vid.height();
				}
				GLFW.glfwSetWindowPos(WINDOW_ID, px[0], py[0]);
				GLFW.glfwSetWindowSize(WINDOW_ID, pw[0], ph[0]);
			}
			else {
				GLFWVidMode vid = GLFW.glfwGetVideoMode(monitor);
				GLFW.glfwSetWindowMonitor(WINDOW_ID, GLFW.glfwGetPrimaryMonitor(), 0, 0, vid.width(), vid.height(), vid.refreshRate());
			}
		}
		else  {
			if(windowed_fullscreen) {
				GLFW.glfwSetWindowSize(WINDOW_ID, origB.width, origB.height);
				GLFW.glfwSetWindowPos(WINDOW_ID, origB.x, origB.y);
			}
			else
				GLFW.glfwSetWindowMonitor(WINDOW_ID, 0, origB.x, origB.y, origB.width, origB.height, 60);
		}
		return this;
	}
	/**
	 * Toggles Fullscreen
	 */
	public final void toggleFullscreen() {
		setFullscreen(!fs);
	}
	/**
	 * Returns true if the window is focused
	 * @return
	 */
	public final boolean isFocused() {
		return focused;
	}
	/**
	 * Returns the monitor the window center is in
	 * @return
	 */
	public final long getCurrentMonitor() {
		if(Thread.currentThread() != mainThread)
			return (long) addPending("getCurrentMonitor");
		PointerBuffer pb = GLFW.glfwGetMonitors();
		for(int i = 0; i < pb.limit(); i++) {
			long monitor = pb.get(i);
			int[] px = new int[1];
			int[] py = new int[1];
			GLFW.glfwGetMonitorPos(monitor, px, py);
			GLFWVidMode b = GLFW.glfwGetVideoMode(monitor);
			int centerX = getPositionX()+getWidth()/2;
			int centerY = getPositionY()+getHeight()/2;
			if(centerX > px[0] && centerX < px[0]+b.width())
				if(centerY > py[0] && centerY < py[0]+b.height())
					return monitor;
		}
		return 0;
	}
	/**
	 * Returns debug information about the monitor
	 * @param monitor
	 * @return
	 */
	public final String getMonitorDebug(long monitor) {
		if(Thread.currentThread() != mainThread)
			return (String) addPending("getMonitorDebug", monitor);
		PointerBuffer pb =  GLFW.glfwGetMonitors();
		int max = pb.limit();
		int current = 0;
		for(int i = 0; i < max; i++)
			if(pb.get(i) == monitor)
				current = i;
		String name = GLFW.glfwGetMonitorName(monitor);
		int[] pw = new int[1];
		int[] ph = new int[1];
		GLFW.glfwGetMonitorPhysicalSize(monitor, pw, ph);
		float[] cw = new float[1];
		float[] ch = new float[1];
		GLFW.glfwGetMonitorContentScale(monitor, cw, ch);
		return "['"+name+"'("+(current+1)+"/"+max+"): PHS: " + pw[0] + ";" + ph[0] + ", DPI: " + cw[0] + ";" + ch[0]+"]";
	}
	//##############################################Input
	public CursorScrollCallback getCallback_CursorScroll() {
		return cursor_scroll;
	}
	/**
	 * Returns the Joystick callback of this window
	 * @return
	 */
	public JoystickInputCallback getCallback_Joystick() {
		return joystick;
	}
	/**
	 * Returns the Key callback of this window
	 * @return
	 */
	public KeyInputCallback getCallback_Key() {
		return key;
	}
	/**
	 * Returns the Text callback of this window
	 * @return
	 */
	public TextInputCallback getCallback_Text() {
		return text;
	}
	/**
	 * Returns the Cursor callback of this window
	 * @return
	 */
	public final CursorCallback getCallback_Cursor() {
		return cursor;
	}
	
	//###############################################STUFF
	/**
	 * returns this Window ID
	 * @return
	 */
	public final long getWINDOW_ID() {
		return WINDOW_ID;
	}
	/**
	 * Binds the default FrameBuffer and sets the openGL viewport.
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 */
	public static void setViewport(int x, int y, int w, int h) {
		GL33.glBindFramebuffer(GL33.GL_FRAMEBUFFER, 0);
		GL33.glViewport(x, y, w, h);
	}
	
	public void adjustViewport() {
		GL33.glViewport(0, 0, bounds.width, bounds.height);
	}
	
	/**
	 * Sets the bounds of the window size
	 * @param minX
	 * @param minY
	 * @param maxX
	 * @param maxY
	 */
	public GraphicWindow setMMSize(int minX, int minY, int maxX, int maxY) {
		if(Thread.currentThread() != mainThread) {
			addPending("setMMSize", minX, minY, maxX, maxY);
			return this;
		}
		GLFW.glfwSetWindowSizeLimits(WINDOW_ID, minX < 0 ? -1 : minX,
												minY < 0 ? -1 : minY,
												maxX < 0 ? -1 : maxX,
												maxY < 0 ? -1 : maxY);
		return this;
	}
	public GraphicWindow setAspectRatio(int n, int d) {
		if(Thread.currentThread() != mainThread) {
			addPending("setAspectRatio", n, d);
			return this;
		}
		GLFW.glfwSetWindowAspectRatio(WINDOW_ID, n, d);
		return this;
	}
	/**
	 * If true
	 * @param workspace_Fullscreen
	 */
	public void setWindowedFullscreen(boolean w) {
		windowed_fullscreen = w;
	}
	public boolean isWindowedFullscreen() {
		return windowed_fullscreen;
	}
	public void setWindowedWorkspaceFullscreen(boolean w) {
		windwed_fullscreen_workspace = w;
	}
	public boolean isWindowedWorkspaceFullscreen() {
		return windwed_fullscreen_workspace;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

}
