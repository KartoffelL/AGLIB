package Kartoffel.Licht.Rendering.Text;

public class SChar {

	public int id;
	public float x;
	public float y;
	public float width;
	public float height;
	
	public float xoffset;
	public float yoffset;
	public float xadvance;
	
	public SChar() {
		
	}
	
	public SChar(int id) {
		this.id = id;
	}
	
	public SChar(int id, float width) {
		this.id = id;
		this.width = width;
	}
	
	public SChar(int id, float x, float y, float z) {
		this.id = id;
		this.width = x;
		this.height = y;
		this.x = z;
	}
	

}
