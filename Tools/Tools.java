package Kartoffel.Licht.Tools;

import static java.lang.Math.toRadians;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.time.Duration;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

import Kartoffel.Licht.Java.BufferedImage;
import Kartoffel.Licht.Java.Color;
import Kartoffel.Licht.Java.namable;

public class Tools {
	
	public static final DecimalFormat df = new DecimalFormat("0000.00");
	
	public static final Matrix4f IDENTITY_MATRIX = new Matrix4f();
	
	public static final Vector4f VECTOR4F = new Vector4f();
	
	public static final Vector3f UP = new Vector3f(0, 1, 0);
	
	public static final Random RANDOM = new Random();
	
	public static final boolean IS_OS_WINDOWS;
	public static final boolean IS_OS_LINUX;
	public static final boolean IS_OS_MAC;
	static {
		String os = System.getProperty("os.name").toLowerCase();
		IS_OS_WINDOWS = os.contains("win");
		IS_OS_LINUX = os.contains("mac");
		IS_OS_MAC = os.contains("lin");
	}
	
	public static final String IPSUM_LIPSUM = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.   \r\n"
			+ "\r\n"
			+ "Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto odio dignissim qui blandit praesent luptatum zzril delenit augue duis dolore te feugait nulla facilisi. Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat.   \r\n"
			+ "\r\n"
			+ "Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat. Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto odio dignissim qui blandit praesent luptatum zzril delenit augue duis dolore te feugait nulla facilisi.   \r\n"
			+ "\r\n"
			+ "Nam liber tempor cum soluta nobis eleifend option congue nihil imperdiet doming id quod mazim placerat facer possim assum. Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat. Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat.   \r\n"
			+ "\r\n"
			+ "Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis.   \r\n"
			+ "\r\n"
			+ "At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, At accusam aliquyam diam diam dolore dolores duo eirmod eos erat, et nonumy sed tempor et et invidunt justo labore Stet clita ea et gubergren, kasd magna no rebum. sanctus sea sed takimata ut vero voluptua. est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat.   \r\n"
			+ "\r\n"
			+ "Consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus.   \r\n"
			+ "\r\n"
			+ "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.   \r\n"
			+ "\r\n"
			+ "Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto odio dignissim qui blandit praesent luptatum zzril delenit augue duis dolore te feugait nulla facilisi. Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat.   \r\n"
			+ "\r\n"
			+ "Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat. Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto odio dignissim qui blandit praesent luptatum zzril delenit augue duis dolore te feugait nulla facilisi.   \r\n"
			+ "\r\n"
			+ "Nam liber tempor cum soluta nobis eleifend option congue nihil imperdiet doming id quod mazim placerat facer possim assum. Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat. Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo";
	
	public static Process restart(String[] args, Class<?> mainClass) throws IOException {
		String b = URLDecoder.decode(mainClass.getProtectionDomain().getCodeSource().getLocation().getPath(), "UTF-8");
		String arg = "";
		String mc = System.getProperty("sun.java.command");
		for(String a : args)
			arg += " "+a;
		
		if(mc.endsWith(".jar")) {
			return Runtime.getRuntime().exec("java -jar \"" + b.substring(1) + "\"" + arg);
		}
		else {
			return Runtime.getRuntime().exec(new String[] {"java", "-cp", b, mc, arg});
		}
	}
	
	public static String formatPositiveDuration(Duration duration) {
	    String s = "";
	    if(duration.toDays() != 0)
	    	s += duration.toDays() + "d:";
	    if(duration.toHours() != 0)
	    	s += duration.toHours() + "h:";
	    if(duration.toMinutes() != 0)
	    	s += duration.toMinutes() + "m:";
	    if(duration.getSeconds() != 0)
	    	s += duration.getSeconds() + "s:";
	    if(duration.toMillis() != 0)
	    	s += duration.toMillis() + "mm:";
	    if(s.length() > 0)
	    if(s.charAt(Math.max(s.length()-1, 0)) == ':')
	    	s = s.substring(0, s.length()-1);
	    return s;
	}
	
	public static int countChars(String str, char c, int start, int end) {
		char[] cs = str.toCharArray();
		int count = 0;
		for(int i = start; i < end; i++) {
			if(cs[i] == c)
				count ++;
		}
		return count;
	}
	
	private static HashMap<Long, Long> dmap = new HashMap<>();
	
	public static boolean every(int x, long key) {
		if(!dmap.containsKey(key))
			dmap.put(key, 0L);
		
		Long val = dmap.get(key);
		dmap.put(key, val+1);
		return val % x == 0;
	}
	
	public static boolean every(double seconds, long key) {
		if(!dmap.containsKey(key))
			dmap.put(key, 0L);
		
		Long val = dmap.get(key);
		if(val < Timer.getTime()) {
			dmap.put(key, Timer.getTime()+(long)(seconds*1000000000L));
			return true;
		}
		
		return false;
	}
	
	public static class every {
		long last = 0; //Some value in the future
		public long getRemaining() {
			return java.lang.Math.max(last-Timer.getTime(), 0);
		}
		public long getRemainingMilli() {
			return java.lang.Math.floorDiv(java.lang.Math.max(last-Timer.getTime(), 0), 1000000);
		}
	}
	
	public static boolean every(int x, every e) {
		e.last++;
		return e.last % x == 0;
	}
	
	public static boolean every(double seconds, every e) {
		if(e.last < Timer.getTime()) {
			e.last = Timer.getTime()+(long)(seconds*1000000000L);
			return true;
		}
		return false;
	}
	
	
	public static boolean containsFlag(int[] flags, int flag) {
		for(int i : flags)
			if(flag == i)
				return true;
		return false;
	}
	
	public static void sortBestResult(List<String> s, String match) {
		s.sort(new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {
				return calculatePoints(o1, match)-calculatePoints(o2, match);
			}
			
		});
	}
	public static void sortBestResultOb(List<?> s, String match) {
		s.sort(new Comparator<Object>() {

			@Override
			public int compare(Object o1, Object o2) {
				return calculatePoints(((namable)o2).getName(), match)-calculatePoints(((namable)o1).getName(), match);
			}
			
		});
	}
	private static int calculatePoints(String a, String b) {
		int points = 0;
		if(a.contentEquals(b))
			points += 1000;
		if(a.equalsIgnoreCase(b))
			points += 500;
		if(a.contains(b))
			points += 400;
		if(a.toLowerCase().contains(b.toLowerCase()))
			points += 300;
		points -= Math.abs(a.length()-b.length())*50;
		return points;
	}
	
	public static String spacing(int depth) {
		String a = "";
		for(int i = 0; i < depth; i++)
			a = a+"    ";
		return a;
	}
	
	public static double[] gaussian(int amount, double distance, boolean oneSided) {
		double spi = Math.sqrt(Math.PI);
		double[] vals = new double[amount];
		double sum = 0;
		for(int i = 0; i < amount; i++) {
			sum += vals[i] = sampleGauss(((double)i/amount-(oneSided ? 0 : -0.5))*distance, ((double)(i+1)/amount-(oneSided ? 0 : -0.5))*distance)/spi;
		}
		for(int i = 0; i < amount; i++) //Correction
			vals[i] /= sum;
		return vals;
	}
	private static double sampleGauss(double x1, double x2) {
		double res = 0;
		double det = 0.001;
		for(double i = x1; i < x2; i += det)
			res += Math.exp(-i*i)*det;
		return res;
	}
	/**
	 * Returns the Log of this Java Instance.:<br>
	 * $bbbHello world!<br>
	 * The 6 characters following the '�' are HEX values indicating the color.<br>
	 * These may only appear after a '\n' character.
	 * 
	 * @return The Color-coded Java Log.
	 */
	public static String getLog() {
		return Logger.log;
	}
	
	/**
	 * Prints a line to Log
	 * @param com
	 */
	public static void writeToLog(String com) {
		Logger.log_in.set(com);
		Logger.log(getTime()+":"+com, new Color(204, 255, 255));
	}
	/**
	 * Prints a line to Error
	 * @param com
	 */
	public static void writeToErr(String com) {
		Logger.log_in.set(com);
		Logger.log(getTime()+":"+com, new Color(255, 20, 20));
	}
	
	/**
	 * Prints a float Array
	 * @param fs
	 */
	public static void printArray(float[] bs) {
		System.out.print("[");
		for(Object o : bs)
			System.out.print(o+";");
		System.out.println("]");
	}
	/*
	 * Prints an boolean Array
	 */
	public static <T> void printArray(boolean[] bs) {
		System.out.print("[");
		for(Object o : bs)
			System.out.print(o+";");
		System.out.println("]");
	}
	/*
	 * Prints an double Array
	 */
	public static <T> void printArray(double[] bs) {
		System.out.print("[");
		for(Object o : bs)
			System.out.print(o+";");
		System.out.println("]");
	}
	
	/*
	 * Prints an byte Array
	 */
	public static <T> void printArray(byte[] bs) {
		System.out.print("[");
		for(Object o : bs)
			System.out.print(o+";");
		System.out.println("]");
	}
	
	/**
	 * Prints an int Array
	 * @param fs
	 */
	public static void printArray(int[] bs) {
		System.out.print("[");
		for(Object o : bs)
			System.out.print(o+";");
		System.out.println("]");
	}
	
	/**
	 * Prints an int Array
	 * @param fs
	 */
	public static void printArray(long[] bs) {
		System.out.print("[");
		for(Object o : bs)
			System.out.print(o+";");
		System.out.println("]");
	}
	
	/**
	 * Prints an int Array
	 * @param fs
	 */
	public static void printArray(char[] bs) {
		System.out.print("[");
		for(Object o : bs)
			System.out.print(o+";");
		System.out.println("]");
	}
	
	/**
	 * Prints an int Array
	 * @param fs
	 */
	public static void printArray(short[] bs) {
		System.out.print("[");
		for(Object o : bs)
			System.out.print(o+";");
		System.out.println("]");
	}
	
	/**
	 * Prints an Object Array
	 * @param fs
	 */
	public static void printArray(Object[] bs) {
		System.out.print("[");
		for(Object o : bs)
			System.out.print(o+";");
		System.out.println("]");
	}
	
	public static void printBits(long l) {
		for(int i = 63; i >= 0; i--)
			System.out.print((l&(1l<<i)) == 0 ? "0" : "1");
		System.out.println();
	}
	public static void printHex(long l) {
		System.out.println(Long.toHexString(l));
	}
	public static void printHex(int l) {
		System.out.println(Integer.toHexString(l));
	}
	
	public static int getRGB_Billing(BufferedImage image, float x, float y, Color def) {
		float nx = x-0.5f;
		float ny = y-0.5f;
		float xx = nx - (int) (nx);
		float yy = ny - (int) (ny);
		Color a = new Color(getColorSafe(image, (int) (x+0.5f), (int) (y+0.5f), def));
		Color b = new Color(getColorSafe(image, (int) (x-0.5f), (int) (y+0.5f), def));
		Color c = new Color(getColorSafe(image, (int) (x+0.5f), (int) (y-0.5f), def));
		Color d = new Color(getColorSafe(image, (int) (x-0.5f), (int) (y-0.5f), def));
		return new Color(
				Math.max(0, Math.min(1, bilin(a.getRed()/255f, b.getRed()/255f, c.getRed()/255f, d.getRed()/255f, xx, yy))),
				Math.max(0, Math.min(1, bilin(a.getGreen()/255f, b.getGreen()/255f, c.getGreen()/255f, d.getGreen()/255f, xx, yy))),
				Math.max(0, Math.min(1, bilin(a.getBlue()/255f, b.getBlue()/255f, c.getBlue()/255f, d.getBlue()/255f, xx, yy)))
				).getRGBA();
	}
	public static float[] getPresiceRGB_Billing(BufferedImage image, float x, float y, Color def) {
		float nx = x-0.5f;
		float ny = y-0.5f;
		float xx = nx - (int) (nx);
		float yy = ny - (int) (ny);
		Color a = new Color(getColorSafe(image, (int) (x+0.5f), (int) (y+0.5f), def));
		Color b = new Color(getColorSafe(image, (int) (x-0.5f), (int) (y+0.5f), def));
		Color c = new Color(getColorSafe(image, (int) (x+0.5f), (int) (y-0.5f), def));
		Color d = new Color(getColorSafe(image, (int) (x-0.5f), (int) (y-0.5f), def));
		return new float[] {
				bilin(a.getRed()/255f, b.getRed()/255f, c.getRed()/255f, d.getRed()/255f, xx, yy),
				bilin(a.getGreen()/255f, b.getGreen()/255f, c.getGreen()/255f, d.getGreen()/255f, xx, yy),
				bilin(a.getBlue()/255f, b.getBlue()/255f, c.getBlue()/255f, d.getBlue()/255f, xx, yy),
				bilin(a.getAlpha()/255f, b.getAlpha()/255f, c.getAlpha()/255f, d.getAlpha()/255f, xx, yy)
			};
	}
	private static int getColorSafe(BufferedImage image, int x, int y, Color d) {
		if(d == null)
			return image.getRGB(mod(x, image.getWidth()), mod(y, image.getHeight()));
		return d.getRGBA();
	}
	public static float bilin(float a, float b, float c, float d, float x, float y) {
		return lerp(lerp(a, b, x), lerp(c, d, x), y);
	}
	public static float lerp(float a, float b, float f) {
		f = mod(f, 1);
		return a*f+b*(1-f);
	}
	public static float mod(float a, float c) {
		float b = a;
		while(b < 0)
			b += c;
		while(b >= c)
			b -= c;
		return b;
	}
	public static int mod(int a, int c) {
		int b = a;
		while(b < 0)
			b += c;
		while(b >= c)
			b -= c;
		return b;
	}
	/**
	 * Returns the Files directory. Null if path has no parent.
	 * 
	 */
	public static String getFileDir(String path) {
		return new File(path).getParent();
	}
	/**
	 * Returns the Files directory. Empty if path has no parent.
	 * 
	 */
	public static String getPathParent(String path) {
		String s = new File(path).getParent();
		return s == null ? "" : (s + "/");
	}
	/**
	 * Returns the File type (with dot)
	 * 
	 */
	public static String getFileType(String file) {
		int i = file.lastIndexOf(".");
		if(i == -1)
			i = file.length()-1;
		return file.substring(i);
	}
	/**
	 * Returns the File name without its Type
	 * 
	 */
	public static String getFileName(String file) {
		int i = file.lastIndexOf(".");
		if(i == -1)
			i = file.length()-1;
		return file.substring(Math.max(file.lastIndexOf("/"), file.lastIndexOf("\\"))+1, i);
	}
	
	
	
	 /**   w  w w   .  d  e   m o   2  s.    c  o m 
     * Get the normal vector of 3 points that lie on a plane.
     * Note: Assumes correct winding order.
     *
     * Source: COMP3421 lecture example code
     *
     * @param p0 first point on plane
     * @param p1 second point on plane
     * @param p2 third point on plane
     * @return Normal vector (perpendicular) to plane face
     */
    public static double[] getNormal(double[] p0, double[] p1, double[] p2) {
        double u[] = { p1[0] - p0[0], p1[1] - p0[1], p1[2] - p0[2] };
        double v[] = { p2[0] - p0[0], p2[1] - p0[1], p2[2] - p0[2] };

        return crossProduct(u, v);
    }

    /**
     * Calculate the cross product between two vectors u and v.
     * Source: COMP3421 lecture example code
     *
     * @param u input u vector
     * @param v input v vector
     * @return Cross product result
     */
    public static double[] crossProduct(double u[], double v[]) {
        double crossProduct[] = new double[3];
        crossProduct[0] = u[1] * v[2] - u[2] * v[1];
        crossProduct[1] = u[2] * v[0] - u[0] * v[2];
        crossProduct[2] = u[0] * v[1] - u[1] * v[0];

        return crossProduct;
    }
    
    
    public static Vector3f rotate2(Vector3f vec, Vector3f rot) {
    	vec.x = 1;
    	vec.y = 1;
    	vec.z = 1;
    	vec.rotateX((float)toRadians(rot.x));
    	vec.rotateY((float)toRadians(rot.y));
    	vec.rotateZ((float)toRadians(rot.z));
    	return vec;
    }
    
    public static Vector3f rotateFrom(Vector3f vec, Vector3f rot) {
    	Quaternionf q = new Quaternionf();
    	q.lookAlong(vec, UP);
    	q.getEulerAnglesXYZ(rot);
    	rot.x = (float) Math.toDegrees(rot.x);
    	rot.y = (float) Math.toDegrees(rot.y);
    	rot.z = (float) Math.toDegrees(rot.z);
    	return rot;
    }
    
	public static double LerpDegrees(double a, double b, double w)
    {
	   double cs = (1-w)*Math.cos(Math.toRadians(a)) + w*Math.cos(Math.toRadians(b));
	   double sn = (1-w)*Math.sin(Math.toRadians(a)) + w*Math.sin(Math.toRadians(b));
	   return Math.toDegrees(Math.atan2(sn, cs));
    }
    
    public static String getStackFullTrace() {
    	String res = "";
    	StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
        for (int i=1; i<stElements.length; i++) {
            StackTraceElement ste = stElements[i];
            res += "["+ste.getClassName()+"."+ste.getMethodName()+"()]:"+ste.getLineNumber()+"\n";
        }
    	return res;
    }
    
    public static String getStackTrace() {
    	StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
        for (int i=2; i<stElements.length; i++) {
            StackTraceElement ste = stElements[i];
            if (!ste.getClassName().equals(Tools.class.getName()) && ste.getClassName().indexOf("java.lang.Thread")!=0) {
                try {
					return "["+Class.forName(ste.getClassName()).getSimpleName()+":"+ste.getLineNumber()+"]";
				} catch (ClassNotFoundException e) {
					return "[Unable to get Class]";
				}
            }
        }
        return null;
    }
    
	public static Object[] reverse(Object[] a) {
    	Object[] b = new Object[a.length];
    	for(int i = 0; i < a.length; i++) {
    		b[i] = a[a.length-i-1];
    	}
		return  b;
    }
	
	public static String[] reverse(String[] a) {
		String[] b = new String[a.length];
    	for(int i = 0; i < a.length; i++) {
    		b[i] = a[a.length-i-1];
    	}
		return  b;
    }
	
	public static int[] set(int[] array, int val) {
		for(int i = 0; i < array.length; i++)
			array[i] = val;
		return array;
	}
	
	public static String null_check(String s) {
		if(s == null)
			return "";
		return s;
	}
	
	
	public static boolean LogEnabled = true;
	public static boolean ErrEnabled = true;
	public static boolean ErrPop = false;
	public static boolean ErrEnabled_StackFullTrace = false;
	public static boolean ErrEnabled_CRASH = false;
	public static boolean RessourceEnabled = false;
	public static boolean ShaderDebugEnabled = true;
	public static boolean VulkanDebugEnabled = true;
	public static boolean KeyDebugEnabled = false;
	public static boolean ConnectionMessage = true;
	public static boolean LevelLoadInfo = false;
	public static boolean Instancing = false;
	public static boolean fontLoading = false;
	public static boolean Socket = false;
	public static boolean prefix = true;
	
	private static String getTime() {
		return "["
				+Calendar.getInstance().get(Calendar.HOUR_OF_DAY)+":"
				+Calendar.getInstance().get(Calendar.MINUTE)+":"
				+Calendar.getInstance().get(Calendar.SECOND)+"]";
	}
	
	public static void log(String s) {
		if(LogEnabled)
			System.out.println(prefix ? getTime()+getStackTrace()+"[Log]: " + s : s);
	}
	public static void vulk(String s) {
		if(VulkanDebugEnabled)
			System.out.println(prefix ? getTime()+getStackTrace()+"[Vulkan]: " + s : s);
	}
	
	public static void err(String s) {
		if(ErrEnabled)
			if(ErrEnabled_StackFullTrace)
				System.err.println(prefix ? getTime()+getStackFullTrace()+"[Err]: " + s : s);
			else
				System.err.println(prefix ? getTime()+getStackTrace()+"[Err]: " + s : s);
		pop(s);
	}
	
	
	public static void Ressource(String s) {
		if(RessourceEnabled)
			System.out.println(prefix ? getTime()+getStackTrace()+"[Res]: " + s : s);
	}
	
	public static void SRL(String s) {
		if(ShaderDebugEnabled)
			if(ErrEnabled_StackFullTrace)
				System.err.println(prefix ? getTime()+getStackFullTrace()+"[Err]: " + s : s);
			else
				System.err.println(prefix ? getTime()+getStackTrace()+"[Err]: " + s : s);
		pop(s);
	}
	
	public static void KeyDeb(String s) {
		if(KeyDebugEnabled)
			System.out.println(prefix ? getTime()+getStackTrace()+"[KD]: " + s : s);
	}
	
	public static void conm(String s) {
		if(ConnectionMessage)
			System.out.println(prefix ? getTime()+getStackTrace()+"[NT]: " + s : s);
	}
	
	public static void LLI(String s) {
		if(LevelLoadInfo)
			System.out.println(prefix ? getTime()+getStackTrace()+"[LL]: " + s : s);
	}
	
	public static void ins(String s) {
		if(Instancing)
			System.out.println(prefix ? getTime()+getStackTrace()+"[INS]: " + s : s);
	}
	
	public static void font(String s) {
		if(fontLoading)
			System.out.println(prefix ? getTime()+getStackTrace()+"[FNT]: " + s : s);
	}
	
	public static void sock(String s) {
		if(Socket)
			System.out.println(prefix ? getTime()+getStackTrace()+"[SCK]: " + s : s);
	}

	private static void pop(String s) {
		if(ErrPop) {
//			if(ErrEnabled_StackFullTrace)
//				popup(prefix ? getTime()+getStackFullTrace()+"[Err]: " + s : s, 2, "Error");
//			else
//				popup(prefix ? getTime()+getStackTrace()+"[Err]: " + s : s, 2, "Error");
		}
		if(ErrEnabled_CRASH)
			throw new RuntimeException();
	}
	
	public static String format(double d) {
		return df.format(d);
	}




}

class bool {
	boolean val = false;
}
