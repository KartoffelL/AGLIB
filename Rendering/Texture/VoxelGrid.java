package Kartoffel.Licht.Rendering.Texture;

import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexParameterf;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_3D;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import java.nio.ByteBuffer;
import java.util.HashMap;

import org.lwjgl.opengl.GL33;
import org.lwjgl.opengl.GL43;
import org.lwjgl.system.MemoryUtil;
/**
 * A Texture is an Object, that stores buffers on the GPU.
 * Its like a Ticket to acces the uploaded Data.
 * Can only be created in the Rendering Thread.
 * 
 * For commercial use.
 * 
 */
public class VoxelGrid implements Renderable{
	private int id;
	private int width;
	private int height;
	private int depth;
	
	private ByteBuffer clearData;
	
	private String name = "Voxel Grid Texture";
	
	/**
	 * type 0 = normal, type 1 = depthstencil, type 2 = HDR
	 * 
	 * @param sx
	 * @param sy
	 * @param type
	 */
	public VoxelGrid(int sx, int sy, int sz) {
			width = sx;
			height = sy;
			depth = sz;
			
			id = glGenTextures();
			
			glBindTexture(GL_TEXTURE_3D, id);
			
			glTexParameterf(GL_TEXTURE_3D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
			glTexParameterf(GL_TEXTURE_3D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
			byte[] b = new byte[sx*sy*sz*4];
			for(int i = 0; i < b.length; i++)
				b[i] = 0;
			b[0] = -1;
			b[1] = 0;
			b[2] = 0;
			b[3] = 50;
			clearData = MemoryUtil.memAlloc(sx*sy*sz*4);
			clearData.put(b);
			clearData.flip();
			GL33.glTexImage3D(GL_TEXTURE_3D, 0, GL43.GL_RGBA32F, sx, sy, sz, 0, GL43.GL_RGBA, GL_UNSIGNED_BYTE, clearData);
	}
	
	
	
	
	public void generateMipmaps() {
		bind(0);
		GL33.glGenerateMipmap(GL33.GL_TEXTURE_3D);
	}
	public VoxelGrid(int bID) {
		this.id = bID;
	}
	
	
	public int getID(int index) {
		return id;
	}
	public void setID(int index, int value) {
		this.id = value;
	}
	
	public void clear() {
		glBindTexture(GL_TEXTURE_3D, id);
		GL33.glTexSubImage3D(GL33.GL_TEXTURE_3D, 0, 0, 0, 0, width, height, depth, GL33.GL_RGBA, GL_UNSIGNED_BYTE, clearData);
	}
	
	public void bind(int sampler) {
		if(sampler >= 0 && sampler <= 31) {
			glActiveTexture(GL_TEXTURE0 + sampler);
			GL43.glBindImageTexture(sampler, id, 0, true, 0, GL33.GL_WRITE_ONLY, GL33.GL_RGBA32F);
			glBindTexture(GL_TEXTURE_3D, id);
		}
	}
	
	@Override
	public void free() {
		GL33.glDeleteTextures(this.id);
		MemoryUtil.memFree(clearData);
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
	
	public int getDepth() {
		return depth;
	}
	
}
