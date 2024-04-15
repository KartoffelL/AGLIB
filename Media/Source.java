package Kartoffel.Licht.Media;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;

import Kartoffel.Licht.Java.freeable;

public class Source implements freeable{
	
	private int sourceID;
	private List<Integer> queue = new ArrayList<>();

	public Source() {
		sourceID = AL10.alGenSources();
	}
	
	public Source setVolume(float val) {
		AL10.alSourcef(sourceID, AL10.AL_GAIN, val);
		return this;
	}
	
	public Source setPitch(float val) {
		AL10.alSourcef(sourceID, AL10.AL_PITCH, val);
		return this;
	}
	
	public Source setPosition(float val1, float val2, float val3) {
		AL10.alSource3f(sourceID, AL10.AL_POSITION, val1, val2, val3);
		return this;
	}
	
	public Source setPosition(Vector3f val) {
		AL10.alSource3f(sourceID, AL10.AL_POSITION, val.x, val.y, val.z);
		return this;
	}
	
	public Source setLooping(boolean val) {
		AL10.alSourcei(sourceID, AL10.AL_LOOPING, val ? AL10.AL_TRUE : AL10.AL_FALSE);
		return this;
	}
	
	public Source setVelocity(float val1, float val2, float val3) {
		AL10.alSource3f(sourceID, AL10.AL_VELOCITY, val1, val2, val3);
		return this;
	}
	
	public Source setVelocity(Vector3f val) {
		AL10.alSource3f(sourceID, AL10.AL_VELOCITY, val.x, val.y, val.z);
		return this;
	}
	
	public boolean isPlaying() {
		return AL10.alGetSourcei(sourceID, AL10.AL_SOURCE_STATE) == AL10.AL_PLAYING;
	}
	
	public int getSourceState() {
		return AL10.alGetSourcei(sourceID, AL10.AL_SOURCE_STATE);
	}
	
	public Source pause() {
		AL10.alSourcePause(sourceID);
		return this;
	}
	
	public Source continuePlaying() {
		AL10.alSourcePlay(sourceID);
		return this;
	}
	
	public Source stop() {
		AL10.alSourceStop(sourceID);
		return this;
	}
	int p1 = 0;
	int p2 = 0;
	ByteBuffer b;
	int frequency = 0;
	boolean stereo;
	boolean doubl;
	boolean flip;
	InputStream is;
	public Source setupFlipFlop(InputStream in, int frequency, double bufferSizeSec, boolean stereo, boolean doubl) {
		stop();
		updateQueue(); 
		if(p1 == 0) {
			b = BufferUtils.createByteBuffer((int) (frequency*bufferSizeSec));
			p1 = Audio.createSound();
			p2 = Audio.createSound();
			this.frequency = frequency;
			this.stereo = stereo;
			this.doubl = doubl;
			this.is = in;
		}
		for(int t = 0; t < 2; t++) {
			int p = t == 0 ? p1 : p2;
			try {
				for(int i = 0; i < b.capacity(); i++)
					b.put(i, (byte) (is.read()-128));
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(doubl)
				Audio.setSound(p, b.asShortBuffer(), frequency, stereo);
			else
				Audio.setSound(p, b, frequency, stereo);
			AL10.alSourceQueueBuffers(sourceID, p);
		}
		flip = false;
		return this;
	}
	
	public Source updateFlipFlop() {
		int num = AL10.alGetSourcei(sourceID, AL10.AL_BUFFERS_PROCESSED);
		if(num != 0) {
			flip = !flip;
			AL10.alSourceUnqueueBuffers(sourceID);
			try {
				for(int i = 0; i < b.capacity(); i++)
					b.put(i, (byte) (is.read()-128));
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(flip) {
				if(doubl)
					Audio.setSound(p1, b.asShortBuffer(), frequency, stereo);
				else
					Audio.setSound(p1, b, frequency, stereo);
				AL10.alSourceQueueBuffers(sourceID, p1);
			}
			if(!flip) {
				if(doubl)
					Audio.setSound(p2, b.asShortBuffer(), frequency, stereo);
				else
					Audio.setSound(p2, b, frequency, stereo);
				AL10.alSourceQueueBuffers(sourceID, p2);
			}
			if(num == 2) { //Queue updating was paused, source stopped playing
				play();
			}
		}
		return this;
	}
	
	public Source play(int buffer) {
		AL10.alSourcei(sourceID, AL10.AL_BUFFER, buffer);
		AL10.alSourcePlay(sourceID);
		return this;
	}
	public Source play() {
		AL10.alSourcePlay(sourceID);
		return this;
	}
	
	public Source queue(int buffer) {
		AL10.alSourceQueueBuffers(sourceID, buffer);
		queue.add(buffer);
		return this;
	}
	public Source updateQueue() {
		AL10.alSourceUnqueueBuffers(sourceID);
		return this;
	}
	
	public int[] getFinishedBuffers() {
		int amount = AL10.alGetSourcei(sourceID, AL10.AL_BUFFERS_PROCESSED);
		if(amount < 0)
			return new int[0];
		int[] res = new int[amount];
		for(int i = 0; i < amount; i++)
			res[i] = queue.remove(0);
		AL10.alSourceUnqueueBuffers(sourceID, res); //Unqueue all processed buffers
		return res;
	}
	
	public void free() {
		stop();
		AL10.alDeleteSources(sourceID);
	}

}

