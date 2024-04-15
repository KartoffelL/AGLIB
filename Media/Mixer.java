package Kartoffel.Licht.Media;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Mixer {
	
	public Instrument INSTRUMENT_SIMPLE = new Instrument() {
		@Override
		public double generate(double frequency, double elapsed) {
			double sin = Math.sin(frequency*elapsed);
			double hit = Math.exp(-8 * elapsed) * sin;
			hit *= Math.min(elapsed*128, 1);
			return hit;
		}
	};
	public Instrument INSTRUMENT_COMPLEX = new Instrument() {
		public double volExp = 2;
		public double ramp = 2;
		public double a = 0;
		public double b = 0.504;
		public double c = 0;
		public double d = 1;
		@Override
		public double generate(double frequency, double elapsed) {
			double vol = Math.exp(-volExp * elapsed);
			vol *= Math.min(elapsed*ramp, 1);
			double x = frequency*elapsed;
			double o = Audio.GEN.generateComplexSine(x, Math.sin(x*a)*Math.PI, Math.sin(x*b)*Math.PI, Math.sin(x*c)*Math.PI, Math.sin(x*d)*Math.PI)/2;
			return o * vol;
		}
	};
	
	public Instrument INSTRUMENT_DEFAULT = INSTRUMENT_SIMPLE;
	
	private HashMap<String, Note> activeNotes = new HashMap<>();
	private List<String> heldByPedal = new ArrayList<>();
	private boolean pedalDown = false;
	static class Note{
		public Instrument instrument;
		public double note;
		public double velocity;
		public long iht = 0;
		public Note(Instrument instrument, double note, double velocity, long iht) {
			super();
			this.instrument = instrument;
			this.note = note;
			this.velocity = velocity;
			this.iht = iht;
		}
	}
	static interface Instrument {
		public double generate(double note, double elapsed);
	}
	public double[] reverb;
	private int index = 0;
	public double reverbLength = 0.0;
	public double arbsorbtion = 0.4;
	private double dyn_vol = 1;
	public byte get(int sample, int frequency) {
		//Notes
		double b = 0;
		double d = 0;
		for(Note n : activeNotes.values()) {
			if(n != null) {
				b += n.instrument.generate(Audio.GEN.getFrequency(n.note), (double)n.iht/frequency)*n.velocity;
				n.iht++;
				d += n.velocity;
			}
		}
		b /= dyn_vol;
		dyn_vol = Math.max(d*.1+dyn_vol*.9, 1);
		
		if(reverb == null)
			reverb = new double[(int) (frequency*reverbLength)];
		if(reverbLength != 0) {
			reverb[index % reverb.length] = (b+reverb[index % reverb.length])*arbsorbtion;
			index++;
			b += reverb[index % reverb.length];
		}
		//Mixing
		
		return Audio.GEN.toByte(b);
	}
	
	public void noteOn(int note, double velocity) {
		String code = getCode(INSTRUMENT_DEFAULT, note);
		if(velocity == 0) {
			activeNotes.remove(code);
			return;
		}
		activeNotes.put(code, new Note(INSTRUMENT_DEFAULT, note, velocity, 0));
	}
	
	public void noteOff(int note) {
		if(pedalDown)
			heldByPedal.add(getCode(INSTRUMENT_DEFAULT, note));
		else
			activeNotes.remove(getCode(INSTRUMENT_DEFAULT, note));
		return;
	}
	private String getCode(Instrument instrument, int note) {
		return instrument.toString()+note;
	}
	
	public void pedalDown() {
		pedalDown = true;
	}
	
	public void pedalUp() {
		pedalDown = true;
		for(String n : heldByPedal)
			activeNotes.remove(n);
		heldByPedal.clear();
	}
}

