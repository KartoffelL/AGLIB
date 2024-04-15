package Kartoffel.Licht.Rendering.Texture;

public interface MultiTexture extends Renderable{
	
	/**
	 * Returns the amount of textures stored
	 * @return
	 */
	public int getAmount();
	
	public void setID(int index, int value);
	

}
