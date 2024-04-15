package Kartoffel.Licht.Rendering.Texture;

import static Kartoffel.Licht.Tools.Tools.Ressource;
import static Kartoffel.Licht.Tools.Tools.err;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_WRAP_R;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import java.nio.ByteBuffer;
import java.util.HashMap;

import org.lwjgl.opengl.GL31;
import org.lwjgl.system.MemoryUtil;

import Kartoffel.Licht.Java.BufferedImage;
import Kartoffel.Licht.Java.ImageIO;
import Kartoffel.Licht.Res.FileLoader;
import Kartoffel.Licht.Tools.TextureUtils;

public class CubeMap implements Renderable{
	private int id;

	private String name = "Texture Cubemap";
	
	/**
	 * Sortment:<br>
	 * 0:	Right<br>
	 * 1:	Left<br>
	 * 2:	Up<br>
	 * 3:	Down<br>
	 * 4:	Back<br>
	 * 5:	Front<br>
	 * 
	 * @param path
	 * @param fileType : A supported file type; '.png'/'.jpg'
	 */
	public CubeMap(String path, String fileType) {
		//Cubemap
		id = glGenTextures();
		glBindTexture(GL_TEXTURE_CUBE_MAP, id);
		
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
		
		
		//Faces
		for(int l = 0; l < 6; l++) {
			BufferedImage bi = null;
			bi = ImageIO.read(FileLoader.getFileD(path+l+"."+fileType));
if(bi == null) {
			err("Could not find image " + l + "! Be careful to number your files from 0-5[.png || .jpg] in the specified directory! Emp. '3.png'"
					+ "\nPath: " + path+l+fileType);
			return;
}
int width = bi.getWidth();
int height = bi.getHeight();

ByteBuffer buffer = TextureUtils.toNativeByteBuffer(bi, 4);
glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + l, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
MemoryUtil.memFree(buffer);
Ressource("loaded Texture " + l);
		
		}
		
	}
	
	public CubeMap(int resolution) {
		//Cubemap
		id = glGenTextures();
		glBindTexture(GL_TEXTURE_CUBE_MAP, id);
		
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
		
		//Faces
		for(int l = 0; l < 6; l++) {
			glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + l, 0, GL_RGBA, resolution, resolution, 0, GL_RGBA, GL_UNSIGNED_BYTE, (ByteBuffer)null);
			Ressource("loaded Texture " + l);
		}
		
	}
	
	public CubeMap() {
		//Cubemap
		id = glGenTextures();
		glBindTexture(GL_TEXTURE_CUBE_MAP, id);
		
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
		
		
		//Faces
		for(int l = 0; l < 6; l++) {
			BufferedImage bi = null;
			bi = ImageIO.read(FileLoader.getFileD("default/skybox/"+l+".png"));
if(bi == null) {
			err("Could not find image " + l + "! Be careful to number your files from 0-5[.png || .jpg] in the specified directory! Emp. '3.png'"
					+ "\nPath: " + "default/skybox/"+l+".png");
			return;
}
int width = bi.getWidth();
int height = bi.getHeight();

ByteBuffer buffer = TextureUtils.toNativeByteBuffer(bi, 4);
glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + l, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
MemoryUtil.memFree(buffer);
Ressource("loaded Texture " + l);
		
		}
		
		
		
		
	}

	@Override
	public void bind(int sampler) {
		if(sampler >= 0 && sampler <= 31) {
			glActiveTexture(GL_TEXTURE0 + sampler);
			glBindTexture(GL_TEXTURE_CUBE_MAP, id);
		}
	}

	@Override
	public void free() {
		GL31.glDeleteTextures(id);
	}
	@Override
	public HashMap<String, Integer> getFlags() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getID(int index) {
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
