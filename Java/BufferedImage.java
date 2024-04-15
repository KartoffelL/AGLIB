package Kartoffel.Licht.Java;

import java.nio.ByteBuffer;

import org.joml.Math;

import Kartoffel.Licht.Tools.Tools;

public class BufferedImage {
	
	public static class Graphics {
		BufferedImage image;

		Graphics(BufferedImage image) {
			super();
			this.image = image;
		}
		
		public void fill(int sx, int sy, int ex, int ey, Color color) {
			int _sx = Math.max(0, Math.min(sx, ex));
			int _sy = Math.max(0, Math.min(sy, ey));
			int _ex = Math.min(image.getWidth(), Math.max(sx, ex));
			int _ey = Math.min(image.getHeight(), Math.max(sy, ey));
			int rgb = color.getRGBA();
			for(int x = _sx; x < _ex; x++) {
				for(int y = _sy; y < _ey; y++) {
					image.setRGBA(x, y, rgb);
				}
			}
		}
		public void fillOval(int sx, int sy, int ex, int ey, Color color) {
			int _sx = Math.max(0, Math.min(sx, ex));
			int _sy = Math.max(0, Math.min(sy, ey));
			int _ex = Math.min(image.getWidth(), Math.max(sy, ey));
			int _ey = Math.min(image.getHeight(), Math.max(sy, ey));
			int rgb = color.getRGBA();
			for(int x = _sx; x < _ex; x++) {
				for(int y = _sy; y < _ey; y++) {
					float px = (float)(x-_sx)/(_ex-_sx)-.5f;
					float py = (float)(y-_sy)/(_ey-_sy)-.5f;
					if(px*px+py*py < .25)
						image.setRGBA(x, y, rgb);
				}
			}
		}
		
	}
	
	

	private int width, height, channels;
	private byte[][][] data;
	
	
	public BufferedImage(int width, int height, int channels) {
		data = new byte[width][height][channels];
		this.width = width;
		this.height = height;
		this.channels = channels;
	}
	
	public BufferedImage(byte[][][] data) {
		this.data = data;
		this.width = data.length;
		this.height = data[0].length;
		this.channels = data[0][0].length;
	}
	
	public BufferedImage(int width, int height, byte...fillColor) {
		data = new byte[width][height][fillColor.length];
		this.width = width;
		this.height = height;
		this.channels = fillColor.length;
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				for(int c = 0; c < channels; c++)
					data[x][y][c] = fillColor[c];
			}
		}
	}
	public BufferedImage(int width, int height, float...fillColor) {
		data = new byte[width][height][fillColor.length];
		this.width = width;
		this.height = height;
		this.channels = fillColor.length;
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				for(int c = 0; c < channels; c++)
					data[x][y][c] = (byte) (fillColor[c]*255);
			}
		}
	}
	
	public BufferedImage(ByteBuffer buff, int w, int h) {
		this.width = w;
		this.height = h;
		this.channels = buff.capacity()/width/height;
		buff.limit(buff.capacity());
		this.data = new byte[width][height][channels];
		for(int i = 0; i < width; i++) {
			for(int l = 0; l < height; l++) {
				for(int c = 0; c < channels; c++) {
					data[i][l][c] = buff.get((i+l*width)*channels+c);
				}
			}
		}
	}
	
	public void put(ByteBuffer bb) {
		put(bb, 0);
	}
	
	public void put(ByteBuffer bb, int offset) {
		for(int i = 0; i < width; i++) {
			for(int l = 0; l < height; l++) {
				for(int c = 0; c < channels; c++) {
					data[i][l][c] = bb.get((i+l*width)*channels+c);
				}
			}
		}
	} 
	
	public Graphics getGraphics() {
		return new Graphics(this);
	}
	
	public int getRGB(int x, int y) {
		int res = 0;
		for(int i = 0; i < channels; i++) {
			res |= data[x][y][i]<<(8*i);
		}
		return res;
	}
	public Color getColor(int x, int y) {
		return new Color(getRGB(x, y));
	}
	public void setRGBA(int x, int y, int val) {
		for(int i = 0; i < channels; i++) {
			data[x][y][i] = (byte) ((val >> (24-i*8)) & 0xFF);
		}
	}
	
	public void setColor(int x, int y, Color color) {
		for(int i = 0; i < channels; i++) {
			data[x][y][i] = color.getComponents()[i];
		}
	}
	public float[] getComponents(int x, int y) {
		float[] res = new float[channels];
		for(int i = 0; i < channels; i++)
			res[i] = (data[x][y][i]&0x0FF)/255f;
		return res;
	}
	public void setComponents(int x, int y, float[] res) {
		for(int i = 0; i < channels; i++)
			data[x][y][i] = (byte) (res[i]*255);
	}
	
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getChannels() {
		return channels;
	}
	
	public void scaleDraw(BufferedImage image_b) {
		scaleDraw(image_b, 0, 0, this.width, this.height, 0, 0, image_b.width, image_b.height);
	}
	
	public void scaleDraw(BufferedImage image_b, int sx, int sy, int ex, int ey) {
		scaleDraw(image_b, sx, sy, ex, ey, 0, 0, image_b.width, image_b.height);
	}

	public void scaleDraw(BufferedImage image_b, int dsx, int dsy, int dex, int dey, int ssx, int ssy, int sex, int sey) {
		for(int i = Math.max(dsx, 0); i < Math.min(dex, width); i++) {
			for(int l = Math.max(dsy, 0); l < Math.min(dey, height); l++) {
				float nxo = (float)(i-dsx)/(dex-dsx);
				float nyo = (float)(l-dsy)/(dey-dsy);
				for(int c = 0; c < Math.min(channels, image_b.channels); c++) {
					data[i][l][c] = (byte) (image_b.data[(int) Tools.mod(nxo*(sex-ssx)+ssx, image_b.getWidth())][(int) Tools.mod(nyo*(sey-ssy)+ssy, image_b.getHeight())][c]);
				}
			}
		}
	}
	
	public byte[][][] getData() {
		return data;
	}
	
	public int[] toIntArray() {
		int[] res = new int[width*height];
		for(int i = 0; i < width; i++) {
			for(int l = 0; l < height; l++) {
				res[i+l*width] = getRGB(i, l);
			}
		}
		return res;
	}
	
	public byte[] toByteArray(int channels) {
		int chan = channels;
		if(chan <= 0)
			chan = this.channels;
		byte[] res = new byte[width*height*chan];
		for(int i = 0; i < width; i++) {
			for(int l = 0; l < height; l++) {
				for(int c = 0; c < Math.min(chan, this.channels); c++) {
					res[(i+l*width)*chan+c] = data[i][l][c];
				}
			}
		}
		return res;
	}

}
