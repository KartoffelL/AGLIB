package Kartoffel.Licht.Rendering.Texture;

import static Kartoffel.Licht.Tools.Tools.err;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Scanner;

import Kartoffel.Licht.Java.Gif;
import Kartoffel.Licht.Java.ImageIO;
import Kartoffel.Licht.Res.FileLoader;
import Kartoffel.Licht.Tools.Timer;
import Kartoffel.Licht.Tools.Tools;
import Kartoffel.Licht.Tools.UUID;

public class Animation implements Renderable{
	private Texture[] frames;
	private long[] delays; //TODO implement delays
	private int pointer;
	private long counterID = UUID.createI();
	
	private double speed;
	
	private String name = "Texture Animation";

	public Animation(File f) {
		this.pointer = 0;
		this.speed = 1;
		
		File[] props = f.listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				if(name.equalsIgnoreCase("prop.txt"))
					return true;
				return false;
			}
		});
		if(props.length > 0) {
			try {
				Scanner sc = new Scanner(new FileReader(props[0]));
				this.speed = 1000000000.0/Double.parseDouble(sc.nextLine());
				sc.close();
			} catch (Exception e) {
				err("Could not load properties for animation: FPS isint a number");
			}
		}
		if(!f.exists()) {
			err("Could not load animation: No Folder found");
			return;
		}
		if(!f.isDirectory()) {
			err("Could not load animation: Has to be a Folder");
			return;
		}
		File[] fils = f.listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File pathname) {
				if(pathname.getName().endsWith(".png"))
					return true;
				return false;
			}
			
		});
		this.frames = new Texture[fils.length];
		
		for(int i = 0; i < fils.length; i++) {
			this.frames[i] = new Texture(FileLoader.getImage(fils[i].getAbsolutePath()));
		}
	}
	
	public Animation(InputStream gif) {
		this.pointer = 0;
		this.speed = 1;
		Gif fils = ImageIO.readGif(gif, 0);
		this.frames = new Texture[fils.images.length];
		
		for(int i = 0; i < fils.images.length; i++) {
			this.frames[i] = new Texture(fils.images[i]);
		}
	}
	public void bind(int sampler) {
		updateTime();
		if(Tools.every(1/speed, counterID)) {
			pointer++;
			if(pointer >= frames.length) pointer = 0;
		}
		frames[pointer].bind(sampler);
	}
	
	private void updateTime() {
		pointer = (int) (((Timer.getTimeMilli()*speed)%(frames.length*1000))/1000.0);
		if(pointer >= frames.length) pointer = 0;
	}
	
	@Override
	public void free() {
		for(Texture t : frames)
			t.free();
	}

	@Override
	public HashMap<String, Integer> getFlags() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int getID(int index) {
		updateTime();
		if(index == -1)
			return frames[pointer].getID(0);
		return frames[index].getID(0);
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	public double getFps() {
		return speed;
	}

	public void setFps(double fps) {
		this.speed = fps;
	}
	
	public long[] getDelays() {
		return delays;
	}

}
