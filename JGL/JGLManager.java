package Kartoffel.Licht.JGL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Kartoffel.Licht.Java.freeable;

public class JGLManager implements freeable{
	
	private HashMap<String, List<JGLComponent>> components = new HashMap<>();
	private JGLFrame jglframe;
	private String current = "default";
	public JGLManager(JGLFrame f) {
		jglframe = f;
	}
	
	public List<JGLComponent> createUI(String path) {
		List<JGLComponent> l = new ArrayList<JGLComponent>();
		components.put(path, l);
		return l;
	}
	public void deleteUI(String path) {
		components.remove(path);
	}
	public List<JGLComponent> getUIList(String path) {
		return components.get(path);
	}
	public void loadUI(String path) {
		jglframe.setComponents(components.get(path));
		for(JGLComponent c : jglframe.getComponents())
			c.pack();
		current = path;
	}
	public void loadDefaultUI() {
		this.loadUI("default");
	}
	public String getCurrent() {
		return current;
	}
	public void setJglframe(JGLFrame jglframe) {
		this.jglframe = jglframe;
	}
	public JGLFrame getJglframe() {
		return jglframe;
	}

	@Override
	public void free() {
		for(List<JGLComponent> l : components.values())
			for(JGLComponent c : l)
				c.dispose();
	}

}
