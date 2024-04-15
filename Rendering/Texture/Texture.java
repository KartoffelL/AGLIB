package Kartoffel.Licht.Rendering.Texture;

import static Kartoffel.Licht.Tools.Tools.Ressource;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameterf;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.GL_DEPTH24_STENCIL8;
import static org.lwjgl.opengl.GL30.GL_DEPTH_STENCIL;
import static org.lwjgl.opengl.GL30.GL_RGBA16F;
import static org.lwjgl.opengl.GL30.GL_UNSIGNED_INT_24_8;

import java.nio.ByteBuffer;
import java.util.HashMap;

import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryUtil;

import Kartoffel.Licht.Java.BufferedImage;
import Kartoffel.Licht.Java.Color;
import Kartoffel.Licht.Tools.TextureUtils;
/**
 * A Texture is an Object, that stores buffers on the GPU.
 * Its like a Ticket to acces the uploaded Data.
 * Can only be created in the Rendering Thread.
 * 
 * For commercial use.
 * 
 */
public class Texture implements Renderable{
	private int id;
	private int width;
	private int height;
	private int internalFormat = 0;
	
	private String name = "Texture";
	
	public static int chnlls(int gl) {
		return gl == GL33.GL_RED ? 1 : gl == GL33.GL_RG ? 2 : gl == GL33.GL_RGB ? 3 : 4;
	}
	public static int chnllsR(int chnnls) {
		return chnnls == 1 ? GL33.GL_RED : chnnls == 2 ? GL33.GL_RG : chnnls == 3 ? GL33.GL_RGB : GL33.GL_RGBA;
	}
	
	public Texture(BufferedImage...bi) {
		this(GL33.GL_RGBA, GL33.GL_NEAREST, bi);
	}
	public Texture(int internalFormat, int filter, BufferedImage... bis) {
		this.internalFormat = internalFormat;
		BufferedImage bi = null;
		if(bis.length == 1) {
			bi = bis[0];
			width = bi.getWidth();
			height = bi.getHeight();
		}else {
			width = 0;
			height = 0;
			for(BufferedImage b : bis) {
				width += b.getWidth();
				height = Math.max(height, b.getHeight());
			}
			bi = new BufferedImage(width, height, 4);
			int cw = 0;
			for(int i = 0; i < bis.length; i++) {
				bi.scaleDraw(bis[i], cw, 0, cw+bis[i].getWidth(), bis[i].getHeight(), 0, 0, bis[i].getWidth(), bis[i].getHeight());
				cw += bis[i].getWidth();
			}
		}
		ByteBuffer buffer = TextureUtils.toNativeByteBuffer(bi, 0);
		id = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, id);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, filter);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, filter);
		glTexImage2D(GL_TEXTURE_2D, 0, internalFormat, width, height, 0, chnllsR(bi.getChannels()), GL_UNSIGNED_BYTE, buffer);
		MemoryUtil.memFree(buffer);
		Ressource("Loaded Texture 2D from Buffered Image: " + bi.getWidth() + " | " + bi.getHeight());
	}
	
	
	public Texture(Color...color) {
		this(GL33.GL_RGBA, GL33.GL_NEAREST, color);
	}
	public Texture(int internalFormat, int filter, Color... color) {
		this.internalFormat = internalFormat;
		BufferedImage bi = new BufferedImage(color.length, 1, chnlls(internalFormat));
		for(int i = 0; i < color.length; i++) {
			if(color[i] != null) {
				bi.setColor(i, 0, color[i]);
			}
		}
		width = bi.getWidth();
		height = bi.getHeight();
		
		ByteBuffer buffer = TextureUtils.toNativeByteBuffer(bi, chnlls(internalFormat));
		id = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, id);
		
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, filter);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, filter);
		glTexImage2D(GL_TEXTURE_2D, 0, internalFormat, width, height, 0, internalFormat, GL_UNSIGNED_BYTE, buffer);
		MemoryUtil.memFree(buffer);
		Ressource("Loaded Texture 2D from Color: " + bi.getWidth() + " | " + bi.getHeight());
	}
	
	
	/**
	 * type 0 = normal, type 1 = depthstencil, type 2 = HDR
	 * 
	 * @param sx
	 * @param sy
	 * @param type
	 */
	public Texture(int sx, int sy, int type) {
			width = sx;
			height = sy;
			
			
			id = glGenTextures();
			
			glBindTexture(GL_TEXTURE_2D, id);
			
			glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
			glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
			if(type == 0) {
				glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, (int[])null);
				internalFormat = GL_RGBA;
				Ressource("Created empy texture: " + sx + "x" + sy);
			}else if(type == 1){
				glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH24_STENCIL8, width, height, 0, GL_DEPTH_STENCIL, GL_UNSIGNED_INT_24_8, (int[])null);
				internalFormat = GL_DEPTH24_STENCIL8;
				Ressource("Created empy DepthStencil texture: " + sx + "x" + sy);
			}else if(type == 2){
				glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F, width, height, 0, GL_RGBA, GL_FLOAT, (int[])null);
				internalFormat = GL_RGBA16F;
				Ressource("Created empy HDR texture: " + sx + "x" + sy);
			}
	}
	
	
	//Mainly for internal stuff
	
	public Texture(ByteBuffer pixels, int sx, int sy) {
		width = sx;
		height = sy;
		internalFormat = GL_RGBA;
		
		id = glGenTextures();
		
		glBindTexture(GL_TEXTURE_2D, id);
		
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, pixels);
		Ressource("Loaded Texture 2D from ByteBuffer: " + sx + " | " + sy);
	}
	
	public Texture(ByteBuffer pixels, int sx, int sy, int channels) {
		width = sx;
		height = sy;
		
		id = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, id);
		
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		int c = channels == 1 ? GL33.GL_RED : channels == 2 ? GL33.GL_RG : channels == 3 ? GL33.GL_RGB : GL_RGBA;
		internalFormat = c;
		glTexImage2D(GL_TEXTURE_2D, 0, c, width, height, 0, c, GL_UNSIGNED_BYTE, pixels);
		Ressource("Loaded Texture 2D from ByteBuffer: " + sx + " | " + sy);
	}
	
	public Texture(ByteBuffer pixels, int sx, int sy, boolean HasAlpha) {
		width = sx;
		height = sy;
		
		id = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, id);
		
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		internalFormat = HasAlpha ? GL_RGBA : GL33.GL_RGB;
		glTexImage2D(GL_TEXTURE_2D, 0, HasAlpha ? GL_RGBA : GL33.GL_RGB, width, height, 0, HasAlpha ? GL_RGBA : GL33.GL_RGB, GL_UNSIGNED_BYTE, pixels);
		Ressource("Loaded Texture 2D from ByteBuffer: " + sx + " | " + sy);
	}

	
	
	public void setFiltering(boolean minNearest, boolean magNearest) {
		glBindTexture(GL_TEXTURE_2D, id);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, minNearest ? GL_NEAREST : GL33.GL_LINEAR);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, magNearest ? GL_NEAREST : GL33.GL_LINEAR);
	}
	
	
	public void generateMipmaps() {
		glBindTexture(GL_TEXTURE_2D, id);
		GL33.glGenerateMipmap(GL_TEXTURE_2D);
	}
	
	
	public void upload(BufferedImage bi) {
		width = bi.getWidth();
		height = bi.getHeight();
		
		ByteBuffer buffer = TextureUtils.toNativeByteBuffer(bi, chnlls(internalFormat));
		glBindTexture(GL_TEXTURE_2D, id);
		
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexImage2D(GL_TEXTURE_2D, 0, internalFormat, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
		MemoryUtil.memFree(buffer);
	}
	
	public void upload(ByteBuffer pixels, int sx, int sy, int type) {
		width = sx;
		height = sy;
		
		pixels.flip();
		
		glBindTexture(GL_TEXTURE_2D, id);
		
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexImage2D(GL_TEXTURE_2D, 0, type, width, height, 0, type, GL_UNSIGNED_BYTE, pixels);
	}
	
	public void upload(byte[] pixels, int sx, int sy, int type) {
		width = sx;
		height = sy;
		glBindTexture(GL_TEXTURE_2D, id);
		
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		throw new RuntimeException("BUG");
	}
	
	public void upload(Color color) {
		BufferedImage bi = new BufferedImage(1, 1, 4);
		bi.setRGBA(0, 0, color.getRGBA());
		width = bi.getWidth();
		height = bi.getHeight();
		
		ByteBuffer buffer = TextureUtils.toNativeByteBuffer(bi, chnlls(internalFormat));
		glBindTexture(GL_TEXTURE_2D, id);
		
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexImage2D(GL_TEXTURE_2D, 0, internalFormat, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
		MemoryUtil.memFree(buffer);
	}
	
	public Texture(int bID) {
		this.id = bID;
	}
	
	public void set(int id, int width, int height, int format) {
		this.width = width;
		this.height = height;
		this.id = id;
		this.internalFormat = format;
	}
	
	
	public int getID(int index) {
		return id;
	}
	public void setID(int index, int value) {
		this.id = value;
	}
	
	public void bind(int sampler) {
		if(sampler >= 0 && sampler <= 31) {
			glActiveTexture(GL_TEXTURE0 + sampler);
			glBindTexture(GL_TEXTURE_2D, id);
		}
	}
	
	@Override
	public void free() {
		GL33.glDeleteTextures(this.id);
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
	
	public int getWidth() {
		return width;
	}
	public int getHeight() {
		return height;
	}

	
}
