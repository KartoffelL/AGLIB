package Kartoffel.Licht.Engine;

public class CubeArray3D {
	
	private short[][][] data;
	private int width, height, depth;

	public CubeArray3D(int width, int height, int depth) {
		data = new short[width][height][depth];
		this.width = width;
		this.height = height;
		this.depth = depth;
	}
	
	public short get(int x, int y, int z) {
		return data[safe(x, width)][safe(y, height)][safe(x, depth)];
	}
	
	public void set(int x, int y, int z, short value) {
		data[safe(x, width)][safe(y, height)][safe(z, depth)] = value;
	}
	
	public int count(short type) {
		int amount = 0;
		for(int i = 0; i < width; i++) {
			for(int l = 0; l < height; l++) {
				for(int k = 0; k < depth; k++) {
					amount += data[i][l][k] == type ? 1 : 0;
				}
			}
		}
		return amount;
	}
	
	public int countN(short type) {
		int amount = 0;
		for(int i = 0; i < width; i++) {
			for(int l = 0; l < height; l++) {
				for(int k = 0; k < depth; k++) {
					amount += data[i][l][k] == type ? 0 : 1;
				}
			}
		}
		return amount;
	}
	
	public void fill(short type) {
		for(int i = 0; i < width; i++) {
			for(int l = 0; l < height; l++) {
				for(int k = 0; k < depth; k++) {
					data[i][l][k] = type;
				}
			}
		}
	}
	
	public static int safe(int x, int max) {
		if(x < 0)
			x += (x/max+1)*max;
		x -= (x/max)*max;
		return x;
	}
	
	public int getWidth() {
		return width;
	}
	public int getHeight() {
		return height;
	}
	public int getDepth() {
		return depth;
	}
	public short[][][] getData() {
		return data;
	}

}
