package com.qunar.deals.util;

public class WhiteBlackList {

	public static String FLAG_WHITE = "white";
	public static String FLAG_BLACK = "black";
	
	private String city;
	
	private String sight;
	
	private String flag;

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getSight() {
		return sight;
	}

	public void setSight(String sight) {
		this.sight = sight;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}
	
	public boolean isWhiteList() {
		return FLAG_WHITE.equals(flag);
	}
	
	public boolean isBlackList() {
		return FLAG_BLACK.equals(flag);
	}
	
	public String getKey() {
		return city + "_" + sight;
	}
}
