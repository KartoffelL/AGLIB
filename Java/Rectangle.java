package Kartoffel.Licht.Java;

public class Rectangle {

	public int x, y, width, height;
	
	public Rectangle() {
		this.x = 0;
		this.y = 0;
		this.width = 0;
		this.height = 0;
	}
	
	public Rectangle(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	public int getWidth() {
		return width;
	}
	public int getHeight() {
		return height;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public void setBounds(Rectangle other) {
		this.x = other.x;
		this.y = other.y;
		this.width = other.width;
		this.height = other.height;
	}
	public boolean contains(int i, int j) {
		return i >= x && i < x+width && j >= y && j < y+height;
	}
	public int getCenterX() {
		return x+width/2;
	}
	public int getCenterY() {
		return y+height/2;
	}
	
	@Override
	public String toString() {
		return "["+x+","+y+","+width+","+height+"]";
	}

}
