
public class Note {
	public long start;
	public long length;
	public int key;
	public int vol;
	
	public Note(int k, long s, long e, int v) {
		start = s;
		length = e-s;
		key = k;
		vol = v;
	}
}
