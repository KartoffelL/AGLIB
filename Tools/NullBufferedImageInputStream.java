package Kartoffel.Licht.Tools;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import Kartoffel.Licht.Java.BufferedImage;
import Kartoffel.Licht.Java.Color;
import Kartoffel.Licht.Java.ImageIO;

public class NullBufferedImageInputStream extends InputStream{

	private static byte[] b;
	static {
		BufferedImage image = new BufferedImage(32, 32, 0);
		for(int i = 0; i < image.getWidth()/2; i++) {
			for(int l = 0; l < image.getHeight()/2; l++) {
				image.setRGBA(i, l, new Color(200, 100, 255).getRGBA());
				image.setRGBA(image.getWidth()-1-i, image.getHeight()-1-l, new Color(200, 100, 255).getRGBA());
				
				image.setRGBA(i, image.getHeight()-1-l, new Color(50, 50, 50).getRGBA());
				image.setRGBA(image.getWidth()-1-i, l, new Color(50, 50, 50).getRGBA());
			}
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.writePNG(baos, image);
		b = baos.toByteArray();
		
	}
	
	private int index = -1;
	
	@Override
	public int read() throws IOException {
		index++;
		if(index < b.length)
			return b[index];
		else
			return -1;
	}

}
