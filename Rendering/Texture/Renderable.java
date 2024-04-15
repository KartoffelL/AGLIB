package Kartoffel.Licht.Rendering.Texture;

import java.util.HashMap;

import Kartoffel.Licht.Java.freeable;
import Kartoffel.Licht.Java.namable;
import Kartoffel.Licht.Java.opengl;

@opengl
public interface Renderable extends freeable, namable{
	
	/**
	 * Binds all Textures to the sampler and its following
	 */
	public void bind(int sampler);
	
	/**
	 * Frees all allocated Memory
	 */
	public void free();
	
	/**
	 * Returns all Flags set to this texture. May return null
	 */
	public HashMap<String, Integer> getFlags();

	/**
	 * Returns the ID linking to the Texture.
	 */
	public int getID(int index);
}
