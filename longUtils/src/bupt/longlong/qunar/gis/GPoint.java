package bupt.longlong.qunar.gis;

public class GPoint{
	public final long x;
	public final long y;

	public GPoint(long x, long y){
		this.x = x;
		this.y = y;
	}

	public String toString() {
		return "[" + this.x + "," + this.y + "]";
	}
}