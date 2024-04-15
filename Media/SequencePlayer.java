package Kartoffel.Licht.Media;

import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

public class SequencePlayer {

	public Mixer mixer = new Mixer();
	public List<event> events = new ArrayList<>();
	static class event {
		int note;
		double velocity;
		int type;
		long tick;
		long tickOffset;
		public event(int note, double velocity, int type, long tick) {
			super();
			this.note = note;
			this.velocity = velocity;
			this.type = type;
			this.tick = tick;
		}
		
	};
	public double second = 0;
	public int eventIndex = 0;
	
	public double bpm = 120;
	public double resolution;
	
	public byte get(int sample, int frequency) {
		while(second <= (double)sample/frequency) {
			event e = events.get(eventIndex);
			if(e.type == 0) //Note On
				mixer.noteOn(e.note, e.velocity);
			else if(e.type == 1) //Note Off
				mixer.noteOff(e.note);
			else if(e.type == 2) //Tempo change 
				bpm = e.velocity;
			second += e.tickOffset/resolution/bpm*60;
			eventIndex++;
		}
		return mixer.get(sample, frequency);
	}
	 public static final int NOTE_ON = 0x90;
	 public static final int NOTE_OFF = 0x80;
	 public static final int ALL_NOTES_OFF = 0x58;
	 public static final int CONTROL_CHANGE = 176;
	 
	 public static final int SET_TEMPO = 0x51;
	 public static final int TIME_SIGNATURE = 0x58;
	public SequencePlayer(Sequence sequence) {
		resolution = sequence.getResolution();
		events.add(new event(0, 0, 10, 0));
        for (Track track :  sequence.getTracks()) {
            for (int i=0; i < track.size(); i++) {
                MidiEvent event = track.get(i);
                
                MidiMessage message = event.getMessage();
                if (message instanceof ShortMessage) {
                    ShortMessage sm = (ShortMessage) message;
                    if (sm.getCommand() == NOTE_ON) {
                        int key = sm.getData1();
                        double velocity = sm.getData2()/127.0;
                        insertEvent(new event(key, velocity, 0, event.getTick()));
                    } else if (sm.getCommand() == NOTE_OFF) {
                        int key = sm.getData1();
                        insertEvent(new event(key, 0, 1, event.getTick()));
                    } else if(sm.getCommand() == ALL_NOTES_OFF) {
                    	
                    }
                    else if(sm.getCommand() == CONTROL_CHANGE) {
                    	
                    }
                }
                else {
                	if(message instanceof MetaMessage) {
                		MetaMessage mmessage = (MetaMessage) message;
                		if(mmessage.getType() == SET_TEMPO) {
                			byte[] d = mmessage.getData();
                			String tempHex = "00";
                			for(byte b : d)
                				tempHex = tempHex+String.format("%02X ", b);
                			tempHex = tempHex.replace(" ", "");
                			int t = Integer.parseInt(tempHex, 16);
                			double bpm = 60000000.0/t;
                			insertEvent(new event(0, bpm, 2, event.getTick()));
                		}
                	}
                }
            }
        }
        //Calculate tick offset
        for(int i = 0; i < events.size(); i++) {
        	long tt = 0;
        	if(i != events.size()-1)
        		tt = events.get(i+1).tick;
        	events.get(i).tickOffset = tt-events.get(i).tick;
        }
	}
	private void insertEvent(event e) {
		long t = 0;
		int i = 0;
		while(t < e.tick && i < events.size()) {
			t = events.get(i).tick;
			i++;
		}
		//i is pointing to the lowest event over e.tick.
		events.add(Math.max(i-1, 0), e);
	}

	
}


