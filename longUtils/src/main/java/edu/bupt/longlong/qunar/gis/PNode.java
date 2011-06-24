package edu.bupt.longlong.qunar.gis;

import java.io.Serializable;

import com.google.gson.Gson;

public class PNode implements Serializable{
	
	private static final long serialVersionUID = 6552560510860787332L;
	public String name;
	public String addr;
	public double lat;
	public double lng;
	
	public PNode(String name, String addr, double lat, double lng) {
		this.name = name;
		this.addr = addr;
		this.lat = lat;
		this.lng = lng;
	}

	public PNode(){}
	
	public String getString(){
		return "{point:["+lat+","+lng+"],name:'"+name+"',address:'"+addr+"'}";
	}
	
	public String toString(){
		return gson.toJson(this);
	}
	
	private final static Gson gson = new Gson();
}
