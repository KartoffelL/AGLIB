package Kartoffel.Licht.Media;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.HashMap;
import java.util.Random;

import org.joml.Vector3f;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALC11;

import Kartoffel.Licht.Tools.Tools;

public class Audio {
	
	final public static generator GEN = new generator();
	
    /** Buffer formats. */
    public static final int
        AL_FORMAT_MONO8    = 0x1100,
        AL_FORMAT_MONO16   = 0x1101,
        AL_FORMAT_STEREO8  = 0x1102,
        AL_FORMAT_STEREO16 = 0x1103;
	
	private static HashMap<String, Integer> buffers = new HashMap<String, Integer>();

	Audio() {
		
	}
	public static void init() {
		long device = ALC10.alcOpenDevice((ByteBuffer) null);
		long contex = ALC10.alcCreateContext(device, (IntBuffer)null);
		ALC10.alcMakeContextCurrent(contex);
		Tools.Ressource("Audio: " + ALC10.alcGetString(device, ALC11.ALC_ALL_DEVICES_SPECIFIER));
		AL.createCapabilities(ALC.getCapabilities());
	}
	
	public static void setPosition(float x, float y, float z) {
		AL10.alListener3f(AL10.AL_POSITION, x, y, z);
	}
	
	public static void setPosition(Vector3f pos) {
		AL10.alListener3f(AL10.AL_POSITION, pos.x, pos.y, pos.z);
	}
	
	public static void setVelocity(float x, float y, float z) {
		AL10.alListener3f(AL10.AL_VELOCITY, x, y, z);
	}
	
	public static void setVelocity(Vector3f pos) {
		AL10.alListener3f(AL10.AL_VELOCITY, pos.x, pos.y, pos.z);
	}
	
	private static Vector3f up = new Vector3f(0, 1, 0);
	
	/**
	 * 
	 * [0..2]	Up vector of Cam
	 * [3..5]	Front(Nose) vector of cam
	 * 
	 * @param f
	 */
	public static void setOrientation(float... f) {
		AL10.alListenerfv(AL10.AL_ORIENTATION, f);
	}

	public static void setOrientation(Vector3f up, Vector3f direction) {
		AL10.alListenerfv(AL10.AL_ORIENTATION, new float[] {up.x, up.y, up.z, direction.x, direction.y, direction.z});
	}
	public static void setUp(float x, float y, float z) {
		up = new Vector3f(x,y,z);
	}
	public static void setUp(Vector3f v) {
		up = v;
	}
	public static void setYaw(float x) {
		AL10.alListenerfv(AL10.AL_ORIENTATION, new float[] {up.x, up.y, up.z, (float) org.joml.Math.cos(org.joml.Math.toRadians(x+90)), 0, (float) org.joml.Math.sin(org.joml.Math.toRadians(x+90))});
	}
	public static void setYawR(float x) {
		AL10.alListenerfv(AL10.AL_ORIENTATION, new float[] {up.x, up.y, up.z, (float) org.joml.Math.cos(x), 0, (float) org.joml.Math.sin(x)});
	}
	
	
	public static int createSound() {
		int buffer = AL10.alGenBuffers();
		Random r = new Random();
		buffers.put(r.nextInt()+"", buffer);
		return buffer;
	}
	
	public static int getSound(String name) {
		if(buffers.get(name) == null) 
			return -1;
		return buffers.get(name);
	}
	
	public static void deleteSound(int buffer) {
		AL10.alDeleteBuffers(buffer);
	}
	
	public static void setSound(int buffer, ShortBuffer data, int sampleRate, boolean stereo) {
		AL10.alBufferData(buffer, stereo ? AL_FORMAT_STEREO16 : AL_FORMAT_MONO16, data, sampleRate);
	}
	
	public static void setSound(int buffer, ByteBuffer data, int sampleRate, boolean stereo) {
		AL10.alBufferData(buffer, stereo ? AL_FORMAT_STEREO8 : AL_FORMAT_MONO8, data, sampleRate);
	}
	
	public static int loadSound(ShortBuffer data, int samlpeRate, boolean stereo) {
		int buffer = AL10.alGenBuffers();
		Random r = new Random();
		buffers.put(r.nextInt()+"", buffer);
		AL10.alBufferData(buffer, stereo ? AL10.AL_FORMAT_STEREO16 : AL10.AL_FORMAT_MONO16, data, samlpeRate);
		
		return buffer;
	}
	
	public static int loadSound(ByteBuffer data, int samlpeRate, boolean stereo) {
		int buffer = AL10.alGenBuffers();
		Random r = new Random();
		buffers.put(r.nextInt()+"", buffer);
		AL10.alBufferData(buffer, stereo ? AL10.AL_FORMAT_STEREO8 : AL10.AL_FORMAT_MONO8, data, samlpeRate);
		return buffer;
	}
	
	
	public static void setSound(int buffer, short[] data, int sampleRate, boolean stereo) {
		AL10.alBufferData(buffer, stereo ? AL_FORMAT_STEREO16 : AL_FORMAT_MONO16, data, sampleRate);
	}
	
	public static void setSound(int buffer, float[] data, int sampleRate, boolean stereo) {
		AL10.alBufferData(buffer, stereo ? AL_FORMAT_STEREO8 : AL_FORMAT_MONO8, data, sampleRate);
	}
	
	public static int loadSound(short[] data, int samlpeRate, boolean stereo) {
		int buffer = AL10.alGenBuffers();
		Random r = new Random();
		buffers.put(r.nextInt()+"", buffer);
		AL10.alBufferData(buffer, stereo ? AL10.AL_FORMAT_STEREO16 : AL10.AL_FORMAT_MONO16, data, samlpeRate);
		
		return buffer;
	}
	
	public static int loadSound(float[] data, int samlpeRate, boolean stereo) {
		int buffer = AL10.alGenBuffers();
		Random r = new Random();
		buffers.put(r.nextInt()+"", buffer);
		AL10.alBufferData(buffer, stereo ? AL10.AL_FORMAT_STEREO8 : AL10.AL_FORMAT_MONO8, data, samlpeRate);
		return buffer;
	}
	
	public static int loadSound(InputStream file) {
		int buffer = AL10.alGenBuffers();
		Random r = new Random();
		buffers.put(r.nextInt()+"", buffer);
		WaveData w = WaveData.create(file);
		AL10.alBufferData(buffer, w.format, w.data, w.samplerate);
		w.free();
		return buffer;
	}
	
	public static void free() {
		for(int buffer : buffers.values()) {
			AL10.alDeleteBuffers(buffer);
		}
		ALC.destroy();
	}
	public static class generator {
		final public Chord CHORD = new Chord();
		public static class Chord {
			final public double[] 
							MAYOR = new double[] {0, 4, 7, 12, .1, .2, .1, .1},
							MINOR = new double[] {0, 3, 7, 12, .1, .2, .1, .1},
							AUGM = new double[] {0, 4, 8, 12, .125, .125, .125, .125},
							DIM = new double[] {0, 3, 6, 12, .125, .125, .125, .125};
		}
		public static int BASE_A = 440;
		public double getFrequency(double note) {
			return BASE_A*Math.pow(2, (double)(note - 49)/12);
		}
		public double generateChord(int note, double a, double b, double c, double d, double time) {
			double f = 0;
			f += generateSine(getFrequency(note+a), time);
			f += generateSine(getFrequency(note+b), time);
			f += generateSine(getFrequency(note+c), time);
			f += generateSine(getFrequency(note+d), time);
			return f/4;
		}
		public double generateChord(double note, double[] nts, double time) {
			double f = 0;
			f += generateSine(getFrequency(note+nts[0]), time)*nts[4];
			f += generateSine(getFrequency(note+nts[1]), time)*nts[5];
			f += generateSine(getFrequency(note+nts[2]), time)*nts[6];
			f += generateSine(getFrequency(note+nts[3]), time)*nts[7];
			return f;
		}
		
		public double generateSine(double frequency, double time) {
			return org.joml.Math.sin(time*org.joml.Math.PI*2*frequency);
		}
		public double generateSquare(double frequency, double time) {
			return (org.joml.Math.floor(((time*frequency+.5)%1)*2)*2-1);
		}
		public double generateTriangle(double frequency, double time) {
			return (org.joml.Math.abs(org.joml.Math.floor(((time*frequency+.25)%1)*2)-((time*frequency-.25) % 1)*4-3));
		}
		public double generateSaw(double frequency, double time) {
			return (((time*frequency+.5) % 1)*2-1);
		}
		public double generateComplexSine(double x, double a, double b, double c, double d) {
			return (org.joml.Math.sin(x + a + org.joml.Math.sin(x + b)) + org.joml.Math.sin(x + c + org.joml.Math.sin(x + d) ));
		}
		public byte toByte(double f) {
			return (byte) (org.joml.Math.max(0, org.joml.Math.min(1, f*.5+.5))*255-128);
		}
		
	}

}
