
public class Note {
	public long start;
	public long length;
	public int key;
	
	public Note(int k, long s, long e) {
		start = s;
		length = e-s;
		key = k;
	}
}
