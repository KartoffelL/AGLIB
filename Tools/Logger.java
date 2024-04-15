package Kartoffel.Licht.Tools;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import Kartoffel.Licht.Input.Formatting;
import Kartoffel.Licht.Java.Color;

/**
 * Class used for Logging
 * @author PC
 *
 */
public class Logger {
	
	//Variables
	public static boolean enableLoggingwithSystemOutErr = true;
	public static String log = "\n";
	final public static KOut log_out = new KOut() {
		public void wrote(int b) {
			if(enableLoggingwithSystemOutErr)
				log_old_out.write(b);
		};
		public String prefix() {
			return Formatting.COLOR_CHANGE+"FFFFFF";
		};
	};
	final public static KOut log_err = new KOut() {
		public void wrote(int b) {
			if(enableLoggingwithSystemOutErr)
				log_old_err.write(b);
		};
		public String prefix() {
			return Formatting.COLOR_CHANGE+"FF0000";
		};
	};
	final public static KIn log_in = new KIn() {
		public int red() {
			try {return log_old_in.read();} catch (IOException e) {}
			return -1;
		};
	};
	final public static PrintStream log_old_out = System.out;
	final public static PrintStream log_old_err = System.err;
	final public static InputStream log_old_in = System.in;
	

	//Loads up as soon this class is loaded
	static {
		init();
	}
	
	public static void muteLogger(Class<?> c) {
		java.util.logging.Logger.getLogger(c.getName()).setLevel(Level.OFF);
	}
	
	//Functions
	public static void log(Object arg) {
		log_out.write(arg+"\n", "");
	}
	public static void log(Object arg, Color c) {
		log_out.write(arg+"\n", Formatting.COLOR_CHANGE+toHex(c.getRed(), c.getGreen(), c.getBlue()));
	}
	public static void log(Object arg, int r, int g, int b) {
		log_out.write(arg+"\n", Formatting.COLOR_CHANGE+toHex(r, g, b));
	}
	
	
	public static String toHex(int r, int g, int b) {
		return String.format("%02X%02X%02X", r, g, b);  
	}
	public static Color fromHex(String hexColor) {
		int r = Integer.valueOf(hexColor.substring(0, 2), 16);
        int g = Integer.valueOf(hexColor.substring(2, 4), 16);
        int b = Integer.valueOf(hexColor.substring(4, 6), 16);
        return new Color(r, g, b);
	}
	public static void init() {
		System.setOut(new PrintStream(log_out));
		System.setErr(new PrintStream(log_err));
		System.setIn(log_in);
	}
	private static void updateLog() {
		if(log.length() > 1500)
			log = log.substring(log.length()-1500, log.length());
	}
	
	
	
	//Classes
	static class KOut extends OutputStream{

		boolean p = true;
		@Override
		final public void write(int b){
			log += (p ? prefix() : "")+(char)b;
			updateLog();
			p = ((char)b) == '\n';
			wrote(b);
		}
		
		final public void write(String b, String prefix){
			log += prefix+b;
			updateLog();
			for(byte c : b.getBytes())
				wrote(c);
		}
		
		public void wrote(int b) {
			
		}
		
		public String prefix() {
			return "";
		}
		
	}
	
	static class KIn extends InputStream {
		
		List<Byte> v = new ArrayList<>();
		
		@Override
		final public int read() throws IOException {
			if(v.size() > 0) {
				byte o = v.get(0);
				v.remove(0);
				return o;
			}
			int r = red();
			if(r != -1) {
				log += (byte)r+"\n";
				return r;
			}
			return -1;
		}
		
		final public void set(String arg) {
			for(byte b : arg.getBytes())
				v.add(b);
		}
		
		public int red() {
			return 0;
			
		}
		
	}
	
}
