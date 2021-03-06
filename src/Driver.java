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
	String song = "song.mid";
    Key[] keys = new Key[120];
	double scale = 10;
	int xshift = -27;
    int span = 100-27;
    float y=-100;
    int bpm = 140;
    int fps = 30;
    int roundR = 6;//rounded corners
    
    boolean flash = true;
    boolean particlespawn = false;
    boolean backgroundcolor = true;
    boolean roundmode = true;
    
    /*                 */
    
    boolean ready = true;
    int colorflux = 255;
    int backflux = 0;
    double tickpersec = 0;
    long[] len = {0,0};
	
	@Override
	public void paint(Graphics g) {
		super.paintComponent(g);
		//background
		g.setColor(new Color(0,0,0));//<-- COLOR CUSTOMIZATION
		g.fillRect(0, 0, 1200, 1200);
		
		//border
		if (backflux>0) {
			int starti = 0;
			for (int i = starti; i < 475; i+=20) {
				int c = (int)(backflux*(i-starti)/(475-starti));
				g.setColor(new Color(0,c,0));//<-- COLOR CUSTOMIZATION
				g.drawOval(-10-i/2, -i/2*9/16, 1200+i, 675+i*9/16);
			}
		}
		
		g.setColor(new Color(colorflux, 255, colorflux));//<-- COLOR CUSTOMIZATION
		//text
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
				//draw the notes
				if (!roundmode)
					g.fillRect((n.key+xshift)*(1200/span), yval, (1200/span), 500-yval);
				else
					g.fillRoundRect((n.key+xshift)*(1200/span), yval, (1200/span), 500-yval, roundR, roundR);
				//first frame
				if (yval+n.length/scale<510) {
					//flash
					if (flash&&n.length>tickpersec*0.3) colorflux = 0;
					//particle spawn
					if (particlespawn) {
						for (int x = 0; x < 5; x++) {
							particles.add(new Particle((n.key+xshift)*(1200/span),500));
						}
					}
					//background flash
					if (n.vol>100&&n.length>tickpersec*0.3) {backflux = n.vol*2;}
				}
			}else {
				//falling notes
				if (!roundmode)
					g.drawRect((n.key+xshift)*(1200/span), yval, (1200/span), (int)(n.length/scale));
				else
					g.drawRoundRect((n.key+xshift)*(1200/span), yval, (1200/span), (int)(n.length/scale), roundR, roundR);
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
			double lagCompensation = 1.026;//<-- DELAY CUSTOMIZATION
			y += lagCompensation*tickpersec/(fps*scale);//1.026 to compensate lag
		}
		
		//color
		if (colorflux<255) colorflux+=30;
		if (colorflux>255) colorflux = 255;
		if (colorflux<0) colorflux = 0;
		
		//background color
		if (backflux>0) backflux-=10;
		if (backflux>255) backflux = 255;
		if (backflux<0) backflux = 0;
		
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
        		notes.add(new Note(i,keys[i].starts.get(j),keys[i].stops.get(j),keys[i].vols.get(j)));
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
	        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(song.substring(0,song.length()-4)+".wav").getAbsoluteFile());
	        Clip clip = AudioSystem.getClip();
	        clip.open(audioInputStream);
	        clip.start();
	    } catch(Exception ex) {
	        System.out.println("Error with playing sound.");
	        ex.printStackTrace();
	    }
	}
}
