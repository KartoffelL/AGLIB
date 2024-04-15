package Kartoffel.Licht.Engine;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

import Kartoffel.Licht.Java.BufferedImage;
import Kartoffel.Licht.Java.Color;
import Kartoffel.Licht.Rendering.Texture.Texture;
import Kartoffel.Licht.Tools.Timer;

public class TimePieChart {
	
	private static List<entry> stamps;
	private static long last = 0;
	private static Texture texture;
	private static BufferedImage bi;
	private static double accum = 1.0/64;
	private static boolean init = false;
	private static boolean initDias = false;
	
	private final static int size = 64;
	/**
	 * Initialize the TPC corea and the texture
	 */
	public static void init() {
		if(init)
			return;
		texture = new Texture(Color.GREEN);
		bi = new BufferedImage(size, size, 1);
		initDias();;
		init = true;
	}
	/**
	 *Initializes the TPC core.
	 */
	public static void initDias() {
		if(initDias)
			return;
		stamps = new ArrayList<>();
		initDias = true;
	}
	
	/**
	 * Starts measuring time
	 */
	public static void measure() {
		last = Timer.getTime();
	}
	private static boolean r_b = false;
	/**
	 * Stamps the measured time by the given name.
	 * @param name
	 */
	public static void stamp(String name) {
		if(!initDias)
			return;
		if(last != 0) {
			r_b = false;
			stamps.forEach(new Consumer<entry>() {

				@Override
				public void accept(entry t) {
					if(name.contentEquals(t.name())) {
						long n = Timer.getTime()-last;
						t.time((long) (n*accum+t.time*(1-accum)));
						r_b = true;
					}
				}
			});
			if(!r_b)
				stamps.add(new entry(Timer.getTime()-last, name));
		}
		last = Timer.getTime();
	}
	/**
	 * Paints the TPC on the CPU and uploads it to the Texture
	 * @return
	 */
	public static int paint() {
		if(!init)
			return 0;
		last = 0;
		double[] vals = new double[stamps.size()];
		double ts = 0;
		for(int i = 0; i < stamps.size(); i++)
			ts += stamps.get(i).time();
		for(int i = 0; i < stamps.size(); i++)
			vals[i] = stamps.get(i).time()/ts;
		
		for(int x = -size/2; x < size/2; x++) {
			for(int y = -size/2; y < size/2; y++) {
				if(x*x+y*y < size*size/4) { //Make it a Circle
					float rot = (float) (Math.atan2(x, y)/Math.PI*.5+.5);
					double c = 0;
					float cc = 0;
					for(int i = 0; i < vals.length; i++)
						if(c < rot) {
							c += vals[i];
							cc++;
						}
					cc /= vals.length;
					bi.setRGBA(x+size/2, y+size/2, new Color(cc, cc, cc).getRGBA());
				}
			}
		}
//		for(int i = 0; i < vals.length; i++) {
//			Graphics g = bi.getGraphics();
//			g.drawString(i+": " + Tools.format(vals[i]), 0, i*10);
//		}
			
		
		texture.upload(bi);
		stamps.clear();
		return texture.getID(0);
	}
	/**
	 * Clears all stamps
	 */
	public static void clear() {
		if(!initDias)
			return;
		stamps.clear();
	}
	/**
	 * Sorts the stamp by time
	 */
	public static void sort() {
		if(!initDias)
			return;
		stamps.sort(new Comparator<entry>() {

			@Override
			public int compare(entry o1, entry o2) {
				return (int) Math.signum(o1.time()-o2.time());
			}
		});
	}
	/**
	 * Returns the time of the stamp at the current index
	 * @param index
	 * @return
	 */
	public static long getTime(int index) {
		if(!initDias)
			return 0;
			return stamps.get(index).time();
	}
	/**
	 * Returns the name of the stamp at the current index
	 * @param index
	 * @return
	 */
	public static String getName(int index) {
		if(!initDias)
			return "use 'TimePieChart.initDias()'";
		return stamps.get(index).name();
	}
	/**
	 * Returns the size of the stamp list
	 * @return
	 */
	public static int getSize() {
		if(!initDias)
			return 0;
		return stamps.size();
	}
	/**
	 * Returns the Texture ID
	 * @return
	 */
	public static int getID() {
		if(!init)
			return 0;
		return texture.getID(0);
	}
	/**
	 * Sets the accumulation size for the sampled data
	 * @param a
	 */
	public static void setAccum(int a) {
		accum = 1.0/a;
	}
	/**
	 * Sums up all times
	 * @return
	 */
	public static long sum() {
		long a = 0;
		for(int i = 0; i < stamps.size(); i++)
			a += stamps.get(i).time;
		return a;
	}

}

class entry {

	long time;
	String name;
	public long time() {
		return time;
	}
	public String name() {
		return name;
	}
	public void time(long i) {
		time = i;
	}
	public entry(long time, String name) {
		super();
		this.time = time;
		this.name = name;
	}
	
	}