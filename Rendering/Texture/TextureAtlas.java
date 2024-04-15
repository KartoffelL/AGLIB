package Kartoffel.Licht.Rendering.Texture;

import static Kartoffel.Licht.Tools.Tools.Ressource;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameterf;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_WRAP_R;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import java.nio.ByteBuffer;
import java.util.HashMap;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryUtil;

import Kartoffel.Licht.Java.BufferedImage;
import Kartoffel.Licht.Java.ImageIO;
import Kartoffel.Licht.Res.FileLoader;
import Kartoffel.Licht.Tools.TextureUtils;

public class TextureAtlas implements Renderable{
	private int TextureID;
	private int size;
	
	private String name = "Texture Atlas";
	
	/**
	 * Have to be arranged in a grid
	 * 
	 * 
	 * @param filenames
	 */
	public TextureAtlas(String[][] filenames, int TextureSize) {
		for(String[] s : filenames)
			if(s.length != filenames.length)
				throw new IllegalStateException("Elements have to be square:: f[2][2] // f[3][3] // f[16][16]");
		size = filenames.length;
		
		BufferedImage[][] bis = new BufferedImage[size][size];
		
		for(int i = 0; i < size; i++) {
			for(int l = 0; l < size; l++) {
				if(filenames[i][l] != null) {
					try {
						bis[i][l] = ImageIO.read(FileLoader.getFileD(filenames[i][l]));
						Ressource("Read File " + i + "|" + l + " - " + filenames[i][l]);
					} catch(Exception e) {
						e.printStackTrace();
					}
					
				}
			}
		}
		BufferedImage bi = new BufferedImage(TextureSize*size, TextureSize*size, 4);
		for(int i = 0; i < size; i++) {
			for(int l = 0; l < size; l++) {
				if(filenames[i][l] != null) {
					for(int x = 0; x < TextureSize; x++) {
						for(int y = 0; y < TextureSize; y++) {
							bi.setRGBA(x+i*TextureSize, y+l*TextureSize, bis[i][l].getRGB(x, y));
						}
					}
				} else {
					for(int x = 0; x < TextureSize; x++) {
						for(int y = 0; y < TextureSize; y++) {
							bi.setRGBA(x+i*TextureSize, y+l*TextureSize, 0);
						}
					}
				}
			}
		}
		int width = bi.getWidth();
		int height = bi.getHeight();
		
		ByteBuffer buffer = TextureUtils.toNativeByteBuffer(bi, 4);
		TextureID = glGenTextures();
		
		glBindTexture(GL_TEXTURE_2D, TextureID);
		
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
		MemoryUtil.memFree(buffer);
		Ressource("Created new Texture Atlas");
	
	}
	
	public TextureAtlas(BufferedImage[][] bis, int TextureSize) {
		for(BufferedImage[] s : bis)
			if(s.length != bis.length)
				throw new IllegalStateException("Elements have to be square:: f[2][2] // f[3][3] // f[16][16]");
		size = bis.length;
		
		BufferedImage bi = new BufferedImage(TextureSize*size, TextureSize*size, 4);
		for(int i = 0; i < size; i++) {
			for(int l = 0; l < size; l++) {
				if(bis[i][l] != null) {
					for(int x = 0; x < TextureSize; x++) {
						for(int y = 0; y < TextureSize; y++) {
							bi.setRGBA(x+i*TextureSize, y+l*TextureSize, bis[i][l].getRGB(x, y));
						}
					}
				} else {
					for(int x = 0; x < TextureSize; x++) {
						for(int y = 0; y < TextureSize; y++) {
							bi.setRGBA(x+i*TextureSize, y+l*TextureSize, 0);
						}
					}
				}
			}
		}
		int width = bi.getWidth();
		int height = bi.getHeight();
		
		ByteBuffer buffer = TextureUtils.toNativeByteBuffer(bi, 4);
		TextureID = glGenTextures();
		
		glBindTexture(GL_TEXTURE_2D, TextureID);
		
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
		MemoryUtil.memFree(buffer);
		Ressource("Created new Texture Atlas");
	
	}
	
	public void generateMipmaps() {
		bind(0);
		GL33.glGenerateMipmap(GL_TEXTURE_2D);
	}
	
	public int getSize() {
		return size;
	}
	
	public int getID(int index) {
		return TextureID;
	}
	public void setID(int index, int id) {
		this.TextureID = id;
	}
	
	public void bind(int sampler) {
		if(sampler >= 0 && sampler <= 31) {
			glActiveTexture(GL_TEXTURE0 + sampler);
			glBindTexture(GL_TEXTURE_2D, TextureID);
		}
	}

	@Override
	public void free() {
		GL11.glDeleteTextures(this.TextureID);
	}

	@Override
	public HashMap<String, Integer> getFlags() {
		// TODO Auto-generated method stub
		return null;
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
