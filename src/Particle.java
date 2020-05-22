
public class Particle {
	public int x;
	public int y;
	public int vx;
	public int vy;
	public int life;
	
	public Particle(int ix, int iy) {
		x = ix;
		y = iy;
		life = 7;
		int spread = 3;
		vx = spread-(int)(Math.random()*spread*2);
		vy = -(int)(Math.random()*spread);
	}
	
	public void update() {
		x+=vx;
		y+=vy;
		vy+=3;
		life--;
	}
	public boolean active() {
		return life>0;
	}
}
