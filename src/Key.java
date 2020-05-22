import java.util.ArrayList;

public class Key {
	public ArrayList<Long> starts;
	public ArrayList<Long> stops;
	public int id;
	
	public Key(int d) {
		id = d;
		starts = new ArrayList<Long>();
		stops = new ArrayList<Long>();
	}
	
	public void addStart(long x) {
		starts.add(x);
	}
	public void addStop(long x) {
		stops.add(x);
	}
}
