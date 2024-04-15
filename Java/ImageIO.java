package Kartoffel.Licht.Java;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.ByteBuffer;

import org.lwjgl.PointerBuffer;
import org.lwjgl.stb.STBIWriteCallbackI;
import org.lwjgl.stb.STBImage;
import org.lwjgl.stb.STBImageWrite;
import org.lwjgl.system.MemoryUtil;

public class ImageIO {
	
	public static BufferedImage fromURL(URL url) throws IOException {
		InputStream is = url.openStream();
		BufferedImage res = read(is);
		is.close();
		return res;
	}
	
	public static BufferedImage read(File f) {
		return read(f, 0);
	}
	public static BufferedImage read(InputStream is) {
		return read(is, 0);
	}
	
	public static BufferedImage read(File f, int desiredChannels) {
		try {
			FileInputStream fis = new FileInputStream(f);
			BufferedImage bi = read(fis, desiredChannels);
			fis.close();
			return bi;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static BufferedImage read(InputStream is, int desiredChannels) {
		try {
			byte[] b = is.readAllBytes();
			ByteBuffer bb = MemoryUtil.memAlloc(b.length);
			bb.put(0, b);
			BufferedImage bi = read(bb, desiredChannels);
			MemoryUtil.memFree(bb);
			return bi;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static Gif readGif(InputStream is, int desiredChannels) {
		try {
			byte[] b = is.readAllBytes();
			ByteBuffer bb = MemoryUtil.memAlloc(b.length);
			bb.put(b);
			Gif bi = readGif(bb, desiredChannels);
			MemoryUtil.memFree(bb);
			return bi;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static BufferedImage read(ByteBuffer buff, int desiredChannels) {
		int[] width = new int[1];
		int[] height = new int[1];
		int[] channels = new int[1];
		ByteBuffer bb = STBImage.stbi_load_from_memory(buff, width, height, channels, desiredChannels);
		if(bb == null)
			throw new RuntimeException("Error: " + STBImage.stbi_failure_reason());
		BufferedImage bi = new BufferedImage(width[0], height[0], channels[0]);
		bi.put(bb);
		STBImage.stbi_image_free(bb);
		return bi;
	}
	
	public static Gif readGif(ByteBuffer buff, int desiredChannels) {
		int[] width = new int[1];
		int[] height = new int[1];
		int[] amount = new int[1];
		int[] channels = new int[1];
		PointerBuffer delays = MemoryUtil.memAllocPointer(1);
		ByteBuffer bb = STBImage.stbi_load_gif_from_memory(buff, delays, width, height, amount, channels, desiredChannels);
		if(bb == null)
			throw new RuntimeException("Error: " + STBImage.stbi_failure_reason());
		Gif gif = new Gif();
		gif.images = new BufferedImage[amount[0]];
		for(int i = 0; i < amount[0]; i++) {
			gif.images[i] = new BufferedImage(width[0], height[0], channels[0]);
			gif.images[i].put(bb, i*width[0]*height[0]*channels[0]);
		}
		STBImage.stbi_image_free(bb);
		return gif;
	}
	
	public static void writeJPG(OutputStream os, BufferedImage image, int quality) {
		byte[] data = image.toByteArray(-1);
		ByteBuffer bb = MemoryUtil.memAlloc(data.length);
		bb.put(0, data);
		bb.flip();
		STBImageWrite.stbi_write_jpg_to_func(new STBIWriteCallbackI() {
			
			@Override
			public void invoke(long context, long data, int size) {
				ByteBuffer buffer = MemoryUtil.memByteBuffer(data, size);
				byte[] a = new byte[size];
				buffer.get(a);
				try {
					os.write(a);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}, 0, image.getWidth(), image.getHeight(), image.getChannels(), bb, quality);
		MemoryUtil.memAlignedFree(bb);
		
		
	}
	
	public static void writePNG(OutputStream os, BufferedImage image) {
		byte[] data = image.toByteArray(-1);
		ByteBuffer bb = MemoryUtil.memAlloc(data.length);
		bb.put(0, data);
		bb.flip();
		STBImageWrite.stbi_write_png_to_func(new STBIWriteCallbackI() {
			
			@Override
			public void invoke(long context, long data, int size) {
				ByteBuffer buffer = MemoryUtil.memByteBuffer(data, size);
				byte[] a = new byte[size];
				buffer.get(a);
				try {
					os.write(a);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}, 0, image.getWidth(), image.getHeight(), image.getChannels(), bb, image.getWidth()*image.getChannels());
		MemoryUtil.memFree(bb);
		
		
	}
	
	public static void writePNG(OutputStream os, ByteBuffer bb, int width, int height, int channels) {
		bb.position(0);
		bb.limit(bb.capacity());
		STBImageWrite.stbi_write_png_to_func(new STBIWriteCallbackI() {
			
			@Override
			public void invoke(long context, long data, int size) {
				ByteBuffer buffer = MemoryUtil.memByteBuffer(data, size);
				byte[] a = new byte[size];
				buffer.get(a);
				try {
					os.write(a);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}, 0, width, height, channels, bb, width*channels);
		
	}

}
