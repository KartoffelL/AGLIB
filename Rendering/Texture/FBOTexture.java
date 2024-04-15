package Kartoffel.Licht.Rendering.Texture;

import static Kartoffel.Licht.Tools.Tools.Ressource;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
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
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_WRAP_R;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.GL_DEPTH24_STENCIL8;
import static org.lwjgl.opengl.GL30.GL_DEPTH_STENCIL;
import static org.lwjgl.opengl.GL30.GL_RGBA16F;
import static org.lwjgl.opengl.GL30.GL_UNSIGNED_INT_24_8;
import static org.lwjgl.opengl.GL32.GL_TEXTURE_2D_MULTISAMPLE;
import static org.lwjgl.opengl.GL32.glTexImage2DMultisample;

import java.nio.ByteBuffer;
import java.util.HashMap;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryUtil;

import Kartoffel.Licht.Java.BufferedImage;
import Kartoffel.Licht.Rendering.GraphicWindow;
import Kartoffel.Licht.Tools.TextureUtils;
/**
 * A Texture, which contains useful utility for the {@link Kartoffel.Licht.Rendering.FrameBuffer} class
 */
public class FBOTexture implements Renderable{
	
	private int id;
	private int width;
	private int height;
	private int type;
	
	private String name = "Texture FBO";
	
	/**
	 * A Texture, which contains useful utility for the {@link Kartoffel.Licht.Rendering.FrameBuffer} class<br>
	 * type 0 = normal, type 1 = depthstencil, type 2 = HDR<br>
	 * 
	 * @param sx
	 * @param sy
	 * @param type
	 */
	public FBOTexture(int sx, int sy, int type, boolean multisampled, int MIN_FILTER, int MAG_FILTER) {
			width = sx;
			height = sy;
			this.type = type;
			
			id = glGenTextures();
			
			if(multisampled) {
				glBindTexture(GL_TEXTURE_2D_MULTISAMPLE, id);
				glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, MIN_FILTER);
				glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, MAG_FILTER);
				glTexImage2DMultisample(GL_TEXTURE_2D_MULTISAMPLE, GraphicWindow.MULTI_SAMPLE, GL_RGBA, width, height, true);
				Ressource("Created empty Multisampled FBO texture: " + sx + "x" + sy + " @" + GraphicWindow.MULTI_SAMPLE + " samples");
				return;
			}
			
			glBindTexture(GL_TEXTURE_2D, id);
			glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, MIN_FILTER);
			glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, MAG_FILTER);
			
			if(type == 0) {
				glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, (int[])null);
				Ressource("Created empy texture: " + sx + "x" + sy);
			}else if(type == 1){
				glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH24_STENCIL8, width, height, 0, GL_DEPTH_STENCIL, GL_UNSIGNED_INT_24_8, (int[])null);
				Ressource("Created empy DepthStencil texture: " + sx + "x" + sy);
			}else if(type == 2){
				glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F, width, height, 0, GL_RGBA, GL_FLOAT, (int[])null);
				Ressource("Created empy HDR (16F) texture: " + sx + "x" + sy);
			}else if(type == 3){
				glTexImage2D(GL_TEXTURE_2D, 0, GL33.GL_RGBA32F, width, height, 0, GL_RGBA, GL_FLOAT, (int[])null);
				Ressource("Created empy HDR (32F) texture: " + sx + "x" + sy);
			}
			
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
			this.name += " (Type:" + type + ")";
	}
	
	/**
	 * resizes the Texture.
	 * 
	 * @param sx
	 * @param sy
	 */
	public void reSize(int sx, int sy) {
		glBindTexture(GL_TEXTURE_2D, id);
		this.width = sx;
		this.height = sy;
		if(type == 0) {
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, (int[])null);
			Ressource("Updated texture: " + sx + "x" + sy);
		}else if(type == 1){
			glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH24_STENCIL8, width, height, 0, GL_DEPTH_STENCIL, GL_UNSIGNED_INT_24_8, (int[])null);
			Ressource("Updted DepthStencil texture: " + sx + "x" + sy);
		}else if(type == 2){
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F, width, height, 0, GL_RGBA, GL_FLOAT, (int[])null);
			Ressource("Uptadet HDR (16F) texture: " + sx + "x" + sy);
		}else if(type == 3){
			glTexImage2D(GL_TEXTURE_2D, 0, GL33.GL_RGBA32F, width, height, 0, GL_RGBA, GL_FLOAT, (int[])null);
			Ressource("UpdatedHDR (32F) texture: " + sx + "x" + sy);
		}
	}
	/**
	 * Applies the local Image to the Texture
	 * @param sx
	 * @param sy
	 */
	public void reTexture(int sx, int sy) {
		reTexture(new BufferedImage(sx, sy, 2));
	}
	/**
	 * Applies the Image to the Texture
	 * @param sx
	 * @param sy
	 */
	public void reTexture(BufferedImage bi) {

		width = bi.getWidth();
		height = bi.getHeight();
		
		ByteBuffer buffer = TextureUtils.toNativeByteBuffer(bi, 4);
		glBindTexture(GL_TEXTURE_2D, id);
		
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
		MemoryUtil.memFree(buffer);
		Ressource("Loaded Texture 2D from Buffered Image: " + bi.getWidth() + " | " + bi.getHeight());
		
		
	}
	
	/**
	 * @return the ID of the Texture
	 */
	public int getId() {
		return id;
	}
	/**
	 * sets the ID of this Texture.<br>
	 * WARNING: The texture, which is uploaded on the GPU, will be lost. thus resulting in a memory leak
	 * @param id
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * binds the Texture to the current sampler
	 */
	public void bind(int sampler) {
		if(sampler >= 0 && sampler <= 31) {
			glActiveTexture(GL_TEXTURE0 + sampler);
			glBindTexture(GL_TEXTURE_2D, id);
		}
	}
	
	/**
	 * Frees the resource uploaded to the GPU
	 */
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
		// TODO Auto-generated method stub
		return id;
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
