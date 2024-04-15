package Kartoffel.Licht.Rendering.Texture;

import java.util.HashMap;

import org.lwjgl.opengl.GL33;

import Kartoffel.Licht.Java.Color;
import Kartoffel.Licht.Media.Video;

/**
 * needs VLC media Player to be installed
 *
 */
public class VideoTexture implements Renderable{
	
	private Texture[] frames;
	private boolean current = false;
	
	private String name = "Texture Video";
	
	private Video bba;
	
	public VideoTexture(Video bba) {
		this.frames = new Texture[2];
		this.frames[0] = new Texture(Color.GREEN);
		this.frames[1] = new Texture(Color.YELLOW);
		this.bba = bba;
	}
	
	
	public void bind(int sampler) {
		current = !current;
		if(bba != null)
			if(bba.getCurrentBuffer() != null)
				frames[current ? 0 : 1].upload(bba.getCurrentBuffer(), bba.getWidth(), bba.getHeight(), GL33.GL_RGB);
			
		frames[current ? 1 : 0].bind(sampler);
	}
	
	@Override
	public void free() {
		for(Texture t : frames)
			t.free();
	}

	@Override
	public HashMap<String, Integer> getFlags() {
		return null;
	}

	@Override
	public int getID(int index) {
		current = !current;
		if(bba != null)
			if(bba.getCurrentBuffer() != null)
				frames[current ? 0 : 1].upload(bba.getCurrentBuffer(), bba.getWidth(), bba.getHeight(), GL33.GL_RGB);
		
		return frames[current ? 1 : 0].getID(index);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}
	
	public Video getVideo() {
		return bba;
	}
	
	public void setVideo(Video bba) {
		this.bba = bba;
	}
	
}
