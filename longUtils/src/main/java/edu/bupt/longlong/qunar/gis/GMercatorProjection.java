package edu.bupt.longlong.qunar.gis;

public class GMercatorProjection{
	public static final GMercatorProjection M24 = new GMercatorProjection(24);
	final double[] pixelsPerLonDegree;
	final double[] pixelsPerLonRadian;
	final GPoint[] bitmapOrigo;
	final long[] numTiles;

	public GMercatorProjection(int zoom){
		this.pixelsPerLonDegree = new double[zoom];
		this.pixelsPerLonRadian = new double[zoom];
		this.bitmapOrigo = new GPoint[zoom];
		this.numTiles = new long[zoom];
		long c = 256L;
		for (int i = 0; i < zoom; ++i) {
			long e = c / 2L;
			this.pixelsPerLonDegree[i] = ((float)c / 360.0F);
			this.pixelsPerLonRadian[i] = (c / 6.283185307179586D);
			this.bitmapOrigo[i] = new GPoint(e, e);
			this.numTiles[i] = c;
			c *= 2L;
		}
	}

	public GPoint fromLatLngToPixel(LatLng ll, int zoom) {
		GPoint offset = this.bitmapOrigo[zoom];
		int x = (int)Math.round(offset.x + ll.lng() * this.pixelsPerLonDegree[zoom]);
		double degree = MathUtil.limit(Math.sin(MathUtil.radianByDegree(ll.lat())), -0.9999D, 0.9999D);
		int y = (int)Math.round(offset.y + 0.5D * Math.log((1.0D + degree) / (1.0D - degree)) * -this.pixelsPerLonRadian[zoom]);
		return new GPoint(x, y);
	}

	public LatLng fromPixelToLatLng(GPoint p, int zoom, boolean unbounded){
		GPoint offset = this.bitmapOrigo[zoom];
		double lng = (p.x - offset.x) / this.pixelsPerLonDegree[zoom];
		double g = (p.y - offset.y) / -this.pixelsPerLonRadian[zoom];
		double lat = MathUtil.degreeByRadian(2.0D * Math.atan(Math.exp(g)) - 1.570796326794897D);
		return new LatLng(lat, lng, unbounded);
	}

	public GPoint tileCheckRange(GPoint p, int zoom, int tilesize) {
		long tiles = this.numTiles[zoom];
		if ((p.y < 0L) || (p.y * tilesize >= tiles)) {
			return null;
		}
		if ((p.x < 0L) || (p.x * tilesize >= tiles)) {
			int e = (int)Math.floor(tiles / tilesize);
			long x = p.x % e;
			if (x < 0L) x += e;
			return new GPoint(x, p.y);
		}
		return p;
	}

	public long getWrapWidth(int zoom) {
		return this.numTiles[zoom];
	}

	public static LatLng fromLine(String line){
		LatLng ll;
		if (line.length() == 20) {
			int x = Integer.parseInt(line.substring(0, 7), 10) * 256 + Integer.parseInt(line.substring(14, 17), 10);
			int y = Integer.parseInt(line.substring(7, 14), 10) * 256 + Integer.parseInt(line.substring(17), 10);
			ll = M24.fromPixelToLatLng(new GPoint(x, y), 22, false);
		} else {
			int x = Integer.parseInt(line.substring(0, 6), 10) * 256 + Integer.parseInt(line.substring(12, 15), 10);
			int y = Integer.parseInt(line.substring(6, 12), 10) * 256 + Integer.parseInt(line.substring(15), 10);
			ll = M24.fromPixelToLatLng(new GPoint(x, y), 17, false);
		}
		return ll;
	}

	public static void main(String[] args) {
		GMercatorProjection gmp = new GMercatorProjection(24);
		System.out.println(gmp.fromLatLngToPixel(new LatLng(1.0D, 1.0D), 2));
		for (long i : gmp.numTiles)
			System.out.println(i);
	}
}