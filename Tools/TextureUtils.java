package Kartoffel.Licht.Tools;

import static Kartoffel.Licht.Tools.Tools.err;

import java.io.File;
import java.io.FileFilter;
import java.nio.ByteBuffer;

import org.lwjgl.system.MemoryUtil;

import Kartoffel.Licht.Java.BufferedImage;
import Kartoffel.Licht.Java.Color;
import Kartoffel.Licht.Java.ImageIO;

public class TextureUtils {

	public static BufferedImage loadSimpleSkybox(String path) {
		File f = new File(path);
		BufferedImage[] ims = new BufferedImage[6];
		int[] width = new int[6];
		int[] height = new int[6];
		if(!f.exists()) {
			err("Could not load Cubemap: it doesnt exist");
			return null;
		}
		if(!f.isDirectory()) {
			err("Could not load Cubemap: it isint a Folder");
			return null;
		}
		
		File[] fils = f.listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File pathname) {
				if(pathname.getAbsolutePath().endsWith(".png"))
					return true;
				return false;
			}
		});
		
		if(fils.length < 6) {
			err("Could not load Cubemap: Cubemap isint complete");
			return null;
		}
		
		//Faces
		for(int l = 0; l < 6; l++) {
			BufferedImage bi;
			bi = ImageIO.read(fils[l], 4);
			width[l] = bi.getWidth();
			height[l] = bi.getHeight();
			ims[l] = bi;
		
		}
		int totalWidth = width[0] + width[1] + width[2] + width[3] + width[4] + width[5];
		int totalHeight = Math.max(Math.max(Math.max(Math.max(Math.max(height[0], height[1]), height[2]), height[3]), height[4]), height[5]);
		BufferedImage result = new BufferedImage(totalWidth, totalHeight, 1);
		
		for(int i = 0; i < 6; i++) {
			for(int x = 0; x < ims[i].getWidth(); x++) {
				for(int y = 0; y < ims[i].getHeight(); y++) {
					int woff = 0;
					for(int k = 0; k < i; k++)
						woff += width[i-1];
					result.setRGBA(x+woff, y, ims[i].getRGB(x, y));
				}
			}
		}
		return result;
	}
	
	public final static ByteBuffer toNativeByteBuffer(BufferedImage bi, int channels) {
		byte[] b = bi.toByteArray(channels);
		ByteBuffer pixels = MemoryUtil.memAlloc(b.length);
		pixels.put(b);
		pixels.flip();
		return pixels;
	}
	
	public final static BufferedImage fromNativeByteBuffer(ByteBuffer buff, int width, int height) {
		return new BufferedImage(buff, width, height);
	}
	
	public final static int CT_HORIZONTAL = 0;
	public final static int CT_VERTICAL = 1;
	
	public static BufferedImage generateColorTransition(Color a, Color b, int res, int direction) {
		BufferedImage bi = new BufferedImage(res, res, 2);
		for(int i = 0; i < res; i++) {
			for(int l = 0; l < res; l++) {
				float c = (float)l/(float)res;
				if(direction == CT_HORIZONTAL)
					bi.setRGBA(l, i, new Color((int)(a.getRed()*c+b.getRed()*(1-c)), (int)(a.getGreen()*c+b.getGreen()*(1-c)), (int)(a.getBlue()*c+b.getBlue()*(1-c))).getRGBA());
				else 
					bi.setRGBA(i, l, new Color((int)(a.getRed()*c+b.getRed()*(1-c)), (int)(a.getGreen()*c+b.getGreen()*(1-c)), (int)(a.getBlue()*c+b.getBlue()*(1-c))).getRGBA());
			}
		}
		return bi;
	}
	public static BufferedImage generateCircle(Color color, int resulation, int boundry) {
		BufferedImage bi = new BufferedImage(resulation, resulation, 2);
		
		double distance = 0;
		float p = 0;
		for(int i = 0; i < resulation; i++) {
			for(int l = 0; l < resulation; l++) {
				distance = Math.sqrt(Math.pow(resulation/2-i, 2)+Math.pow(resulation/2-l, 2));
				
				if(boundry > 0) {
					p = (float) (-(Math.pow((distance-resulation/2), 2)/boundry)+1);
					p = Math.max(0, Math.min(p, 1));
				}
				
				Color col = new Color(color.getRGBA());
				col = new Color(col.getRed()/255f, col.getGreen()/255f, col.getBlue()/255f, 1-p);
				if(distance <= resulation/2)
					bi.setRGBA(i, l, col.getRGBA());
				else
					bi.setRGBA(i, l, 0);
			}
		}
		return bi;
	}
	
	public static BufferedImage generateColorWheel(double beginn, int resulation, boolean white_center, int boundry) {
		BufferedImage bi = new BufferedImage(resulation, resulation, 4);
		
		double distance = 0;
		double rotation = 0;
		float p = 0;
		for(int i = 0; i < resulation; i++) {
			for(int l = 0; l < resulation; l++) {
				distance = Math.sqrt(Math.pow(resulation/2-i, 2)+Math.pow(resulation/2-l, 2));
				rotation = Math.atan2(
						resulation/2-i,
						resulation/2-l
						)+Math.toRadians(beginn);
				
				if(boundry > 0) {
					p = (float) (-(Math.pow((distance-resulation/2), 2)/boundry)+1);
					p = Math.max(0, Math.min(p, 1));
				}
				
				Color col = Color.getHSBColor((float) (rotation/Math.PI/2), white_center ? (float) (distance/(resulation/2)) : 1, 1);
				col = new Color(col.getRed()/255f, col.getGreen()/255f, col.getBlue()/255f, 1-p);
				if(distance <= resulation/2)
					bi.setRGBA(i, l, col.getRGBA());
			}
		}
		
		
		
		return bi;
	}
	
	
	public static BufferedImage generate3DBlock(Color a, Color b, int border, int resulationX, int resulationY) {
		BufferedImage bi = new BufferedImage(resulationX, resulationY, 4);
		for(int i = 0; i < resulationX; i++) {
			for(int l = 0; l < resulationY; l++) {
				if(i < border || i >= resulationX-border || l < border || l >= resulationY-border)
					bi.setRGBA(i, l, a.getRGBA());
				else
					bi.setRGBA(i, l, b.getRGBA());
			}
		}
		
		
		
		return bi;
	}
	
	
	
}
