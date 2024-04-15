package Kartoffel.Licht.Media;

import java.nio.ShortBuffer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;

public class Sogen {
	
	final public static int SAMPLE_RATE = 44100;
	final public static double SECONDS = 0.05;
	
	public static ShortBuffer get() {
		short[] data = new short[(int) (SAMPLE_RATE*SECONDS)];
		for(int i = 0; i < data.length; i++) {
			data[i] = (short) (Math.sin(i/100)*32767);
		}
		// ("Generated sound with " + data.length + " Samples and " + data.length/SAMPLE_RATE + " seconds")
		return ShortBuffer.wrap(data).flip();
	}
	
	public static void play() throws Exception {
		AudioFormat af = new AudioFormat(SAMPLE_RATE, 8, 1, true, true);
		SourceDataLine line = AudioSystem.getSourceDataLine(af);
		line.open(af, SAMPLE_RATE);
		line.start();
		
		for(int il = -20; il < 20; il++) {
		byte[] b = new byte[(int) (SAMPLE_RATE*SECONDS)];
		double exp = ((double) il - 1) / 12d;
	    double f = 440d * Math.pow(2d, exp);
	    
	    for (int i = 0; i < b.length-4; i+= 5) {
          double period = (double)SAMPLE_RATE / f;
          double angle = 2.0 * Math.PI * i / period;
          b[i+0] = (byte)(Math.sin(angle) * 127f);
          b[i+1] = (byte)(50 * 127f);
          b[i+2] = (byte)(20 * 127f);
          b[i+3] = (byte)(30 * 127f);
          b[i+4] = (byte)(40 * 120f);
        }
	    
		
		line.write(b, 0, b.length);
		Thread.sleep((long) (1000*SECONDS));
		}
		
		
		line.drain();
		line.close();
		
	}

}
