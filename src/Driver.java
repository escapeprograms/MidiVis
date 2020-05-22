import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.WindowConstants;


public class Driver extends JPanel implements ActionListener {
	ArrayList<Note> notes = new ArrayList<Note>();
	ArrayList<Particle> particles = new ArrayList<Particle>();
	/*Customizables*/
	String song = "Winter Wind x Megalovania.mid";
    Key[] keys = new Key[120];
	double scale = 10;
	int xshift = -27;
    int span = 100-27;
    float y=-1500;
    int bpm = 140;
    int fps = 30;
    boolean flash = true;
    boolean particlespawn = true;
    /*                 */
    
    boolean ready = true;
    int colorflux = 255;
    double tickpersec = 0;
    long[] len = {0,0};
	
	@Override
	public void paint(Graphics g) {
		super.paintComponent(g);
		g.setColor(new Color(0,0,0));
		g.fillRect(0, 0, 1200, 1200);
		g.setColor(new Color(255, colorflux, colorflux));
		
		g.setFont(new Font("Comic Sans MS", 0, 20));
		g.drawString("\""+song.substring(0,song.length()-4)+"\" by Visionist", 10, 600);
		g.drawString("Midi Visualizer by Visionist", 10, 625);
		
		//draw notes
		for (int i = 0; i < notes.size(); i++) {
			Note n = notes.get(i);
			int yval = (int)(y-n.start/scale-n.length/scale);
			if (yval<-n.length/scale||yval>500) continue;
			//g.drawString(notes.get(i).length+"", (n.key+xshift)*(1200/span), y-(int)(n.start/scale));
			//note drawing
			if (yval+n.length/scale>500) {
				//active notes
				if (ready) {ready = false;playSound();}
				g.fillRect((n.key+xshift)*(1200/span), yval, (1200/span), 500-yval);
				//first frame
				if (yval+n.length/scale<510) {
					//flash
					if (flash&&n.length>tickpersec*0.3) colorflux = (int) (255*(1-n.length/(1.5*tickpersec)));
					//particle spawn
					if (particlespawn) {
						for (int x = 0; x < 5; x++) {
							particles.add(new Particle((n.key+xshift)*(1200/span),500));
						}
					}
				}
			}else {
				//falling notes
				g.drawRect((n.key+xshift)*(1200/span), yval, (1200/span), (int)(n.length/scale));
			}
		}
		
		//barrier
		g.drawLine(0, 500, 1200, 500);
		
		//particles
		for (int i = 0; i < particles.size(); i++) {
			Particle p = particles.get(i);
			g.fillOval(p.x, p.y, 4, 4);
		}
	}

	public void update() {
		/*double tperq = 60.0/bpm;
		double dist = 1080/scale;
		double speed = dist/tperq/40;
		y+= speed;*/
		//System.out.println("time "+len[1]);
		//System.out.println("length "+len[0]);
		if (len[1] > 0) {
			//System.out.println(y);
			double lagCompensation = 1.026;
			y += lagCompensation*tickpersec/(fps*scale);//1.026 to compensate lag
		}
		
		//color
		if (colorflux<255) colorflux+=15;
		if (colorflux>255) colorflux = 255;
		if (colorflux<0) colorflux = 0;
		
		//particles
		for (int i = 0; i < particles.size(); i++) {
			Particle p = particles.get(i);
			p.update();
			if (!p.active()) {
				particles.remove(i);
				i--;
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		update();
		repaint();
	}

    public static void main(String[] args) throws Exception {
    	Driver driver = new Driver();
    }
    
	public Driver() throws Exception {
		JFrame f = new JFrame();
		f.setTitle("Midi Visualizer by Visionist");
		f.setSize(1200,675);
		f.setBackground(Color.BLACK);
		f.setResizable(false);
		
		f.add(this);
		
		Timer t = new Timer(1000/fps, this);//40 fps
		t.start();
		f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		f.setVisible(true);
		
		//setup
        for (int i = 0; i < keys.length; i++) {
        	keys[i] = new Key(i);
        }
        
        ParseMidi.createParse(song, keys, len);
        tickpersec = 1000*1000*((double)len[0]/len[1]);
        
        for (int i = 0; i < keys.length; i++) {
        	for (int j = 0; j < keys[i].starts.size(); j++) {
        		notes.add(new Note(i,keys[i].starts.get(j),keys[i].stops.get(j)));
        	}
        }
        for (int i = 0; i < 1000; i++) {
        	//System.out.println(i+" "+notes.get(i).length);
			//System.out.println(notes.get(i).start);
        }
        
        //playSound();
	}

	Timer t;
	
	
	public void playSound() {
	    try {
	        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("Winter Wind x Megalovania.wav").getAbsoluteFile());
	        Clip clip = AudioSystem.getClip();
	        clip.open(audioInputStream);
	        clip.start();
	    } catch(Exception ex) {
	        System.out.println("Error with playing sound.");
	        ex.printStackTrace();
	    }
	}
}