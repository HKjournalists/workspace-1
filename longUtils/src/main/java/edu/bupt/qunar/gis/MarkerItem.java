package edu.bupt.qunar.gis;

public class MarkerItem{
	public final double lat;
	public final double lng;
	public final String name;
	public final String addr;
	public final String equs;

	MarkerItem(String name, String addr, double lat, double lng){
		this.name = name;
		this.addr = addr;
		this.lat = lat;
		this.lng = lng;

		this.equs = name + addr + lat + lng;
	}

	public boolean equals(Object o){
		if (o == this)
			return true;
		if (!(o instanceof MarkerItem))
			return false;
		MarkerItem other = (MarkerItem)o;
		return this.equs.equals(other.equs);
	}

	public int hashCode(){
		return this.equs.hashCode(); 
	}

	public String toString(){
		return "MarkerItem{lat: " + this.lat + ",lng: " + this.lng + ",name: " + this.name + ",addr: " + this.addr + "}";
	}
}