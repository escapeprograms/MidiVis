import java.util.ArrayList;

public class Key {
	public ArrayList<Long> starts;
	public ArrayList<Long> stops;
	public ArrayList<Integer> vols;
	public int id;
	
	public Key(int d) {
		id = d;
		starts = new ArrayList<Long>();
		stops = new ArrayList<Long>();
		vols = new ArrayList<Integer>();
	}
	
	public void addStart(long x, int y) {
		starts.add(x);
		vols.add(y);
	}
	public void addStop(long x) {
		stops.add(x);
	}
}
