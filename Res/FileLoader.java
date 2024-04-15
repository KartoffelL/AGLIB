package Kartoffel.Licht.Res;

import static Kartoffel.Licht.Tools.Tools.err;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;

import Kartoffel.Licht.Java.BufferedImage;
import Kartoffel.Licht.Java.ImageIO;
import Kartoffel.Licht.Tools.Tools;

public class FileLoader {
	
	final private static String prefix = "Kartoffel/Licht/Res/";
	
	final public static InputStream getRAS(String name) {
		InputStream s = FileLoader.class.getClassLoader().getResourceAsStream(name);
		if(s == null)
			s = FileLoader.class.getClassLoader().getResourceAsStream("src/"+name);
		return s;
	}
	
	
	/**
	 * Returns an asset from AGLIB as an InputStream
	 * @param name
	 * @return
	 */
	final public static InputStream getFileD(String name) {
		if(name.getBytes()[1] == ':') {
			try {return new FileInputStream(new File(name));} catch (FileNotFoundException e) {
				Tools.err("Could not load File: " + name + "! " + e.getMessage());
			}
		}else {
			InputStream in = getRAS(prefix+name);
			if(in != null)
				return in;
			Tools.err("Could not load File: " + prefix+name + "! " + in);
		}
		return null; //"No File Found By Toolingonator";
	}
	
	final public static BufferedImage getImageD(String name) {
		return ImageIO.read(getFileD(name), 4);
	}
	/**
	 * Returns an asset from AGLIB as an String
	 * @param name
	 * @return
	 */
	final public static String readFileD(String Filename) {
		StringBuilder string = new StringBuilder();
		BufferedReader br;
		try {
			InputStream is = getFileD(Filename);
			if(is == null)
				return "";
			br = new BufferedReader(new InputStreamReader(is));
			String line;
			while((line = br.readLine()) != null) {
				string.append(line);
				string.append("\n");
			}
			br.close();
		}catch (IOException e) {
			err("Courld not read File! " + e.getMessage());
		}
		return string.toString();
	}
	
	/**
	 * Returns an asset as an ByteBuffer
	 * @param name
	 * @return
	 */
	final public static ByteBuffer getBufferD(String name) {
		InputStream in = getFileD(name);
		try {
			byte[] bytes = in.readAllBytes();
			ByteBuffer bb = ByteBuffer.allocateDirect(bytes.length);
			bb.put(bytes);
			bb.flip();
			return bb;
		} catch (IOException e) {
			err("Could not load Buffer! " + e.getMessage());
		}
		return null;
	}
	
	
	/**
	 * Returns an asset from AGLIB as an InputStream
	 * @param name
	 * @return
	 */
	final public static InputStream getFile(String name) {
		if(name.getBytes()[1] == ':') {
			try {return new FileInputStream(new File(name));} catch (FileNotFoundException e) {
				Tools.err("Could not load File: " + name + "; ");
			}
		}else {
			InputStream in = getRAS(name);
			if(in != null)
				return in;
			Tools.err("Could not load File: " + name + "!" + in);
		}
		return null; //"No File Found By Toolingonator";
	}
	
	final public static BufferedImage getImage(String name) {
		return ImageIO.read(getFile(name), 4);
	}
	/**
	 * Returns an asset from AGLIB as an String
	 * @param name
	 * @return
	 */
	final public static String readFile(String Filename) {
		StringBuilder string = new StringBuilder();
		BufferedReader br;
		try {
			InputStream is = getFile(Filename);
			if(is == null)
				return "";
			br = new BufferedReader(new InputStreamReader(is));
			String line;
			while((line = br.readLine()) != null) {
				string.append(line);
				string.append("\n");
			}
			br.close();
		}catch (IOException e) {
			err("Courld not read File! " + e.getMessage());
		}
		return string.toString();
	}
	
	/**
	 * Returns an asset as an ByteBuffer
	 * @param name
	 * @return
	 */
	final public static ByteBuffer getBuffer(String name) {
		InputStream in = getFile(name);
		try {
			byte[] bytes = in.readAllBytes();
			ByteBuffer bb = ByteBuffer.allocateDirect(bytes.length);
			bb.put(bytes);
			bb.flip();
			return bb;
		} catch (IOException e) {
			err("Could not load Buffer! " + e.getMessage());
		}
		return null;
	}
	
	public static String read(InputStream is) {
		try {
			return new String(is.readAllBytes());
		} catch (IOException e) {
			return e.getMessage();
		}
	}
}
