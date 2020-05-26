import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

public class ParseMidi {
    public static final int NOTE_ON = 0x90;
    public static final int NOTE_OFF = 0x80;

    public static void createParse (String pathname, Key[] keys, long[] len) throws Exception  {
        Sequence sequence = MidiSystem.getSequence(new File(pathname));
        len[0] = sequence.getTickLength();
        len[1] = sequence.getMicrosecondLength();
        
        
        int highestKey = 0;
        int lowestKey = 120;
        int trackNumber = 0;
        for (Track track :  sequence.getTracks()) {
            trackNumber++;
            System.out.println("Track " + trackNumber + ": size = " + track.size());
            System.out.println();
            for (int i=0; i < track.size(); i++) { 
                MidiEvent event = track.get(i);
                
                long tick = event.getTick();
                //System.out.print("@" + event.getTick() + " ");
                MidiMessage message = event.getMessage();
                if (message instanceof ShortMessage) {
                    ShortMessage sm = (ShortMessage) message;
                    //System.out.print("Channel: " + sm.getChannel() + " ");
                    if (sm.getCommand() == NOTE_ON) {
                        int key = sm.getData1();
                        int velocity = sm.getData2();
                        //System.out.println("Note on, " + noteName + octave + " key=" + key + " velocity: " + velocity);
                        keys[key].addStart(tick, velocity);
                        if (key>highestKey) highestKey=key;
                        if (key<lowestKey) lowestKey=key;
                    } else if (sm.getCommand() == NOTE_OFF) {
                        int key = sm.getData1();
                        int velocity = sm.getData2();
                        //System.out.println("Note off, " + noteName + octave + " key=" + key + " velocity: " + velocity);
                        keys[key].addStop(tick);
                    } else {
                        //System.out.println("Command:" + sm.getCommand());
                    }
                } else {
                    //System.out.println("Other message: " + message.getClass());
                }
            }
            
            System.out.println("lo "+lowestKey);
            System.out.println("hi "+highestKey);
        }
    }
}