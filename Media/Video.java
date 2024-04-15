package Kartoffel.Licht.Media;

import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;

import Kartoffel.Licht.Java.freeable;
import Kartoffel.Licht.Res.Downloader;
import Kartoffel.Licht.Tools.Tools;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.media.callback.seekable.SeekableCallbackMedia;
import uk.co.caprica.vlcj.player.base.ControlsApi;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.callback.DefaultAudioCallbackAdapter;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.videosurface.VideoSurface;
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.BufferFormat;
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.BufferFormatCallback;
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.RenderCallback;

/**
 * needs VLC media Player to be installed
 *
 */
public class Video implements freeable {
	
	private ByteBuffer current;
	private ByteBuffer currentAudio;
	private int width, height;
	private EmbeddedMediaPlayer emp;
	private static MediaPlayerFactory mpf;
	@SuppressWarnings("unused")
	private BufferFormatCallback bfc;
	@SuppressWarnings("unused")
	private SeekableCallbackMedia nsism;
	
	private Source audioOutput;
	
	public static void init() {
		try {
			String OS = System.getProperty("os.name");
			OS = OS.startsWith("Windows") ? "Windows" : OS.startsWith("Mac") ? "MacOSX" : "Linux";
			Downloader.downloadNativeLibrary_VLC(OS);
		} catch (Exception e) {
			Tools.err("Failed to download VLC! " + e.getMessage());
		}
		mpf = new MediaPlayerFactory("--quiet");
	}
	
	public static void freeLib() {
		mpf.release();
		mpf = null;
	}
	
	
	public Video(String mrl, String...options) throws Exception {
		this.v(mrl, options);
	}

	private void v(String mrl, String[] options) throws Exception {
		if(mpf == null)
			Video.init();
		emp = mpf.mediaPlayers().newEmbeddedMediaPlayer();
		VideoSurface vs = mpf.videoSurfaces().newVideoSurface(
				bfc = new BufferFormatCallback() {
			@Override
			public BufferFormat getBufferFormat(int sourceWidth, int sourceHeight) {
				width = sourceWidth;
				height = sourceHeight;
				return new BufferFormat("RV24", sourceWidth, sourceHeight, new int[] {3*sourceWidth}, new int[] {sourceHeight});
			}
			@Override
			public void allocatedBuffers(ByteBuffer[] buffers) {}
		}, new RenderCallback() {
			
			@Override
			public void display(MediaPlayer mediaPlayer, ByteBuffer[] nativeBuffers, BufferFormat bufferFormat) {
				current = nativeBuffers[0];
			}
		}, true);
		emp.audio().callback("S16N", 44100, 1, new DefaultAudioCallbackAdapter(2) {
			
			@Override
			protected void onPlay(MediaPlayer mediaPlayer, byte[] data, int sampleCount, long pts) {
				if(currentAudio == null)
					currentAudio = BufferUtils.createByteBuffer(sampleCount*2);
				currentAudio.put(0, data);
				if(audioOutput != null) {
					int buffer = AL10.alGenBuffers();
					AL10.alBufferData(buffer, AL10.AL_FORMAT_MONO16, currentAudio, 44100);
					audioOutput.queue(buffer);
					if(audioOutput.getSourceState() != AL10.AL_PLAYING)
						audioOutput.play();
					int[] finished = audioOutput.getFinishedBuffers();
					for(int i : finished)
						Audio.deleteSound(i);
				}
			
			}
		}, true);
		
		emp.videoSurface().set(vs);
		emp.media().prepare(mrl, options);
		emp.controls().setRepeat(true);
	}
	

	@Override
	public void free() {
		stop();
		emp.release();
	}
	
	public ByteBuffer getCurrentBuffer() {
		return current;
	}
	public ByteBuffer getCurrentAudioBuffer() {
		return currentAudio;
	}
	
	public EmbeddedMediaPlayer getEmp() {
		return emp;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getWidth() {
		return width;
	}
	public MediaPlayerFactory getMpf() {
		return mpf;
	}
	public uk.co.caprica.vlcj.player.base.MediaApi getCurrentMedia() {
		return emp.media();
	}
	public ControlsApi getCurrentControls() {
		return emp.controls();
	}
	public uk.co.caprica.vlcj.player.base.AudioApi getCurrentAudio() {
		return emp.audio();
	}
	public Video play() {
		emp.controls().play();
		return this;
	}
	public Video stop() {
		emp.controls().stop();
		return this;
	}
	public Video setTime(long time) {
		emp.controls().setTime(time);
		return this;
	}
	public Video setRate(float rate) {
		emp.controls().setRate(rate);
		return this;
	}
	public Video setPosition(float pos) {
		emp.controls().setPosition(height);
		return this;
	}
	public Video setPaused(boolean paused) {
		emp.controls().setPause(true);
		return this;
	}
	public Video setRepeat(boolean repeat) {
		emp.controls().setRepeat(repeat);
		return this;
	}
	public Video setVolume(int volume) {
		emp.audio().setVolume(volume);
		return this;
	}
	public Video setMute(boolean mute) {
		emp.audio().setMute(mute);
		return this;
	}
	public Video setAudioOutput(Source audioOutput) {
		this.audioOutput = audioOutput;
		audioOutput.setLooping(false); //Looping has to be set to false
		return this;
	}
	public Source getAudioOutput() {
		return audioOutput;
	}
}