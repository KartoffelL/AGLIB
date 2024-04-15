package Kartoffel.Licht.Input;

import static Kartoffel.Licht.Tools.Tools.KeyDeb;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.opengl.GL11.GL_TRUE;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

import org.joml.Math;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallback;

import Kartoffel.Licht.Tools.Timer;
/**
 * A static class handling the Key callback.
 * Automatically set up by the Rendering class
 * 
 * @author PC
 *
 */
public class KeyInputCallback extends GLFWKeyCallback implements InputCall{
	
	
	private ConcurrentHashMap<Integer, Boolean> Keys = new ConcurrentHashMap<Integer, Boolean>();
	private ConcurrentHashMap<Integer, Boolean> P_Keys = new ConcurrentHashMap<Integer, Boolean>();
	private ConcurrentHashMap<Integer, Long> D_Keys = new ConcurrentHashMap<Integer, Long>();
	private TextInputCallback text;
	private long id;
	
	public KeyInputCallback(TextInputCallback t, long id) {
		this.text = t;
		this.id = id;
	}
	@Override
	public String toString() {
		return Keys.values().size()+"";
	}

	@Override
	public void invoke(long window, int key, int scancode, int action, int mods) {
		if (action == GLFW_PRESS) {
			KeyDeb("Key Pressed: " + key);
			Keys.put(key, true);
		}
		else if(action == GLFW_RELEASE) {
			Keys.put(key, false);
			KeyDeb("Key Released: " + key);
		}
		if(text.getCurrentInstance() == null)
			return;
		
		//For writing
			if(isKeyDown("LEFT_CONTROL") && isKeyDown("C"))
				Clipboard.set(text.getCurrentInstance().getSelectedText());
			if(isKeyDown("LEFT_CONTROL") && isKeyDown("V")) {
				text.getCurrentInstance().setSelectedText(Clipboard.get());
			}
			if(isKeyDown("LEFT_CONTROL") && isKeyDown("A")) {
				text.getCurrentInstance().setSelection(0, text.getCurrentInstance().getLength());
			}
			if(isKeyDown("LEFT"))
				if(text.getCurrentInstance().shouldDrawIndicator()) {
					int sel = text.getCurrentInstance().getSelect_1();
					sel = Math.max(0, sel-1);
					text.getCurrentInstance().setSelection(sel, sel);
				}
				else {
					int sel = text.getCurrentInstance().getSelect_1();
					text.getCurrentInstance().setSelection(sel, sel);
				}
			if(isKeyDown("right"))
				if(text.getCurrentInstance().shouldDrawIndicator()) {
					int sel = text.getCurrentInstance().getSelect_1();
					sel = Math.min(text.getCurrentInstance().getLength(), sel+1);
					text.getCurrentInstance().setSelection(sel, sel);
				}
				else {
					int sel = text.getCurrentInstance().getSelect_2();
					text.getCurrentInstance().setSelection(sel, sel);
				}
				
			if(isKeyDown("BACKSPACE"))
				if(text.getCurrentInstance().getSelectedText().length() > 0)
					text.getCurrentInstance().setSelectedText("");
				else
					text.getCurrentInstance().deleteLatest();
			if(isKeyDown("DELETE"))
				text.getCurrentInstance().setText("");
		//-----//
	}
	/**
	 * Returns true if the key is currently pressed down
	 * 
	 * @param k
	 * @return
	 */
	public boolean isKeyDown(String k) {
		int g = GLFWKeys.valueOf("GLFW_KEY_" + k.toUpperCase()).getValue();
		if(Keys.get(g) == null)
			return false;
		return Keys.get(g);
	}
	/**
	 * Returns true if the key has been pressed.
	 * 
	 * @param k
	 * @return
	 */
	public boolean isKeyPressed(String k) {
		int g = GLFWKeys.valueOf("GLFW_KEY_" + k.toUpperCase()).getValue();
		boolean is = true;
		if(!Keys.containsKey(g))
			return false;
		boolean value = Keys.get(g);
		if(P_Keys.containsKey(g))
			is = P_Keys.get(g) != value;
		return value && is;
	}
	/**
	 * Returns true if the key has been released.
	 * 
	 * @param k
	 * @return
	 */
	public boolean isKeyReleased(String k) {
		int g = GLFWKeys.valueOf("GLFW_KEY_" + k.toUpperCase()).getValue();
		boolean is = true;
		if(!Keys.containsKey(g))
			return false;
		boolean value = Keys.get(g);
		if(P_Keys.containsKey(g))
			is = P_Keys.get(g) != value;
		return !value && is;
	}
	/**
	 * Returns true if the key has been double tapped.
	 * 
	 * @param k
	 * @return
	 */
	public boolean isKeyPressedDouble(String k, long th_mili) {
		int g = GLFWKeys.valueOf("GLFW_KEY_" + k.toUpperCase()).getValue();
		boolean d = false;
		if(isKeyPressed(k)) {
			if(Timer.getTimeMilli()-D_Keys.getOrDefault(g, 0l) < th_mili) {
				d = true;
			}
		}
		return d;
	}
	
	/**
	 * Returns the GLFW Key Code
	 * @param k
	 * @return
	 */
	public static int getKeyCode(String k) {
		return GLFWKeys.valueOf("GLFW_KEY_" + k.toUpperCase()).getValue();
	}
	
	/**
	 * Returns true if the mouse button is currently pressed down
	 * 0 == Left, 
	 * 1 == Right,
	 * 2 = middle,
	 * @param key
	 * @return
	 */
	public boolean isMouseButtonDown(int key) {
		return GLFW.glfwGetMouseButton(id, key) == GL_TRUE;
	}
	/**
	 * Returns true if the key has been pressed.
	 * 0 == Left, 
	 * 1 == Right,
	 * 2 = middle,
	 * @param k
	 * @return
	 */
	public boolean isMouseButtonPressed(int key) {
		int g = GLFWKeys.valueOf("GLFW_KEY_MOUSE_" + key).getValue();
		boolean is = true;
		boolean value = isMouseButtonDown(key);
		if(P_Keys.containsKey(g))
			is = P_Keys.get(g) != value;
		return value && is;
	}
	/**
	 * Returns true if the Key has been released.
	 * 0 == Left, 
	 * 1 == Right,
	 * 2 = middle,
	 * @param k
	 * @return
	 */
	public boolean isMouseButtonReleased(int key) {
		int g = GLFWKeys.valueOf("GLFW_KEY_MOUSE_" + key).getValue();
		boolean is = true;
		boolean value = isMouseButtonDown(key);
		if(P_Keys.containsKey(g))
			is = P_Keys.get(g) != value;
		return !value && is;
	}
	/**
	 * Returns true if the key has been double tapped.
	 * 
	 * 0 == Left, 
	 * 1 == Right,
	 * 2 = middle,
	 * @param k
	 * @return
	 */
	public boolean isMouseButtonPressedDouble(int key, long th_mili) {
		boolean d = false;
		int kk = getKeyCode("MOUSE_" + key);
		if(isMouseButtonPressed(key)) {
			if(Timer.getTimeMilli()-D_Keys.getOrDefault(kk, 0l) < th_mili) {
				d = true;
			}
		}
		return d;
	}


	@Override
	public void update() {
		Keys.forEach(new BiConsumer<Integer, Boolean>() {
			@Override
			public void accept(Integer t, Boolean u) {
				if(u != P_Keys.get(t))
					D_Keys.put(t, Timer.getTimeMilli());
				P_Keys.put(t, u);
			}
		});
		for(int key = 0; key < 3; key++) {
			int i = GLFWKeys.valueOf("GLFW_KEY_MOUSE_" + key).getValue();
			boolean imd = isMouseButtonDown(key);
			if(P_Keys.containsKey(i)) {
				boolean b = P_Keys.get(i);
				if(imd != b)
					D_Keys.put(i, Timer.getTimeMilli());
			}
			P_Keys.put(i, isMouseButtonDown(key));
		}
		
	}
	

}

class Clipboard {
	
	public static String get() {
	    return GLFW.glfwGetClipboardString(1);
	}
	
	public static void set(String text) {
		GLFW.glfwSetClipboardString(0, text);
	}
	
}


