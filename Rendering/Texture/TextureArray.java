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
import Kartoffel.Licht.Java.Color;
import Kartoffel.Licht.Java.ImageIO;
import Kartoffel.Licht.Res.FileLoader;
import Kartoffel.Licht.Tools.TextureUtils;
/**
 * A Texture is an Object, that stores buffers on the GPU.
 * Its like a Ticket to acces the uploaded Data.
 * Can only be created in the Rendering Thread.
 * 
 * 
 */
public class TextureArray implements MultiTexture{
	private int[] id = new int[] {-1, -1, -1, -1, -1, -1, -1, -1};
	private int amount;
	private final int MAX_AMOUNT = 8;
	private int width;
	private int height;
	
	private String name = "Texture Array";
	
	/**
	 * A Texture is an Object, that stores buffers on the GPU.
	 * Its like a Ticket to access the uploaded Data.
	 * Can only be created in the Rendering Thread.
	 * 
	 * @param FileName  - The by the File class accessible Texture thats Readable by the ImageI0 class
	 */
	public TextureArray(String... fis) {
		if(fis.length < 1)
			return;
		
		for(String FileName : fis) {
			if(amount >= MAX_AMOUNT)
				break;
			if(FileName == null) {
				id[amount] = -1;
				continue;
			}
			BufferedImage bi;
			
			bi = ImageIO.read(FileLoader.getFileD(FileName));
			width = bi.getWidth();
			height = bi.getHeight();
			
			ByteBuffer buffer = TextureUtils.toNativeByteBuffer(bi, 4);
			
			glBindTexture(GL_TEXTURE_2D, id[amount]);
			
			glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
			glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
			glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
			glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
			glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
			MemoryUtil.memFree(buffer);
			Ressource("Loaded Texture 2D: " + FileName);
			amount++;
		}
	}
	
	
	public TextureArray(BufferedImage...bis) {
		if(bis.length < 1)
			return;
		
		for(BufferedImage bi : bis) {
			if(amount >= MAX_AMOUNT)
				break;
			
			if(bi == null) {
				id[amount] = glGenTextures();
				continue;
			}
			
			width = bi.getWidth();
			height = bi.getHeight();
			
			ByteBuffer buffer = TextureUtils.toNativeByteBuffer(bi, 4);
			id[amount] = glGenTextures();
			
			glBindTexture(GL_TEXTURE_2D, id[amount]);
			
			glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
			glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
			MemoryUtil.memFree(buffer);
			Ressource("Loaded Texture 2D from Buffered Image: " + bi.getWidth() + " | " + bi.getHeight());
			amount++;
		}
	}
	
	
	
	public TextureArray(Color...colors) {
		for(Color c : colors) {
			if(amount >= MAX_AMOUNT)
				break;
			
			ByteBuffer pixels = MemoryUtil.memAlloc(4);
			int pixel = c.getRGBA();
			pixels.put((byte) ((pixel >> 16) & 0xFF));		//RED
			pixels.put((byte) ((pixel >> 8) & 0xFF));		//GREEN
			pixels.put((byte) (pixel & 0xFF));				//BLUE
			pixels.put((byte) ((pixel >> 24) & 0xFF));
			pixels.flip();
			
			id[amount] = glGenTextures();
			
			glBindTexture(GL_TEXTURE_2D, id[amount]);
			
			glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
			glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, 1, 1, 0, GL_RGBA, GL_UNSIGNED_BYTE, pixels);
			MemoryUtil.memFree(pixels);
			Ressource("Created Color Texture(1x1) with " + colors.length + " Colors");
			amount++;
		}
	}
	
	public TextureArray(int...renderables) {
		for(int idd : renderables) {
			if(amount >= MAX_AMOUNT)
				break;
			id[amount] = idd;
			amount++;
		}
	}
	public TextureArray(Renderable...renderables) {
		for(Renderable idd : renderables) {
			if(amount >= MAX_AMOUNT)
				break;
			int a = 1;
			if(idd instanceof MultiTexture)
				a = ((MultiTexture) idd).getAmount();
			for(int i = 0; i < a; i++) {
				id[amount] = idd.getID(i);
				amount++;
			}
		}
	}
	public TextureArray(int amount) {
		this.amount = amount;
	}
	
	public void generateMipmaps() {
		bind(0);
		GL33.glGenerateMipmap(GL_TEXTURE_2D);
	}
	
	/**
	 * Binds all textures from the given Value(included)
	 */
	public void bind(int from) {
		int index = 0;
		for(int i : id) {
			if(i != -1) {
				glActiveTexture(GL_TEXTURE0 + from + index);
				glBindTexture(GL_TEXTURE_2D, i);
			}
			index++;
		}
	}
	
	
	
	public int getAmount() {
		return amount;
	}
	
	public void setAmount(int amount) {
		this.amount = amount;
	}
	
	@Override
	public void free() {
		GL11.glDeleteTextures(this.id);
	}


	@Override
	public HashMap<String, Integer> getFlags() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public int getID(int index) {
		return id[index];
	}
	
	@Override
	public void setID(int index, int value) {
		this.id[index] = value;
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
