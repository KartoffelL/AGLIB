package Kartoffel.Licht.Tools;

import java.util.HashMap;

public class Timer {

	public static long getTime() {
		return System.nanoTime();
	}
	public static long getTimeMilli() {
		return System.currentTimeMillis();
	}
	public static double getTimeSeconds() {
		return System.nanoTime()/1000000000.0;
	}
	public static float getTimeSecondsF() {
		return System.nanoTime()/1000000000.0f;
	}
	
	private static HashMap<Integer, Long> time = new HashMap<Integer, Long>();
	public static void startTimer(int id) {
		time.put(id, getTime());
	}
	/**
	 * @return in seconds
	 */
	public static double stopTimer(int id) {
		return (getTime()-time.get(id))/1000000000f;
	}

}
