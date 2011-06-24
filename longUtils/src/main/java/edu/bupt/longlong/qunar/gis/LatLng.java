package edu.bupt.longlong.qunar.gis;

public class LatLng{
	public final double lat;
	public final double lng;

	public LatLng(double lat, double lng){
		this(lat, lng, false);
	}

	public LatLng(double lat, double lng, boolean unbounded) {
		if (!(unbounded)) {
			lat = MathUtil.limit(lat, -90.0D, 90.0D);
			lng = MathUtil.loopToRange(lng, -180.0D, 180.0D);
		}
		this.lat = lat;
		this.lng = lng;
	}

	public double lat() {
		return this.lat;
	}

	public double lng() {
		return this.lng;
	}

	public String toString() {
		return this.lat + "," + this.lng;
	}
}