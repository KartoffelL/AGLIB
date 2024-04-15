package Kartoffel.Licht.Rendering.Text;

import Kartoffel.Licht.Java.namable;
import Kartoffel.Licht.Rendering.Texture.Texture;

public interface FontProvider extends namable{
	
	final public static int NO_CHAR = 32;
	
	public SChar getChar(int codepoint, int FLAGS);
	
	public Texture getTexture(int FLAGS);
	
	public int getHeight();

}
