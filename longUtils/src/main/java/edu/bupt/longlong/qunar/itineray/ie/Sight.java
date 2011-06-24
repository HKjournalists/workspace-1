/*
 * @(#)Sight.java	0.01 2010/3/18
 * 	
 * Copyright (c)  2010-2012 Qunar, Inc.
 * All rights reserved.
 */

package edu.bupt.longlong.qunar.itineray.ie;

import java.util.HashMap;
import java.util.Iterator;

/**
 * A sight.
 *
 * @version 	0.01 18 Mar 2010
 * @author 	xunxin
 */
public class Sight implements Cloneable{
	
	/**  earth's radius */
	public final static double EARTH_RADIUS = 6378.137;

	public static String TYPE_SIGHT = "景区";
	public static String TYPE_AREA = "区县";
	public static String TYPE_CITY = "城市";
	public static String TYPE_SPOT = "景点";
	public static String TYPE_COUNTRY = "国家";
	public static String TYPE_REGION = "地区";
	public static String TYPE_PROVINCE = "省份";
	public static String TYPE_FOREIGN = "外国";
	public static String TYPE_CONTINENT = "大洲";
	
	private String id;

	private String name;

	private String type;
	
	private Double latitude;

	private Double longitude;

	private String parentSightId;
	
	
	
	private int startPos = 0;
	private int endPos = 0;

	/* build sight tree */
	private Sight parentSight = null;
	private Sight city = null;
	private Sight country = null;

	public static Sight getUpLevelByCity(Sight city) {
		if ("中国".equals(city.getCountry().getName())) {
			Sight upLevelSight = city.getParentSight();
			if (upLevelSight != null && "省份".equals(upLevelSight.type)) return upLevelSight;
			return null;
		} else {
			return city.getCountry();
		}
	}
	
	public Sight getProvince() {
		if (getCountry() == null) return null;
		if ("中国".equals(getCountry().getName())) {
			Sight p = null;
			Sight t = this;
			while( (p = t.getParentSight()) != null) {
				if (p.isProvince()) return p;
				t = p;
				p = null;
			}
		} 
		return null;
	}
	public Sight getContinent() {
		if (getCountry() == null) return null;
		if (!"中国".equals(getCountry().getName())) {
			Sight p = null;
			Sight t = this;
			while( (p = t.getParentSight()) != null) {
				if (p.isContinent()) return p;
				t = p;
				p = null;
			}
		} 
		return null;
	}
	public Sight getRegion() {
		if (getCountry() == null) return null;
		if ("中国".equals(getCountry().getName())) {
			Sight p = null;
			Sight t = this;
			while( (p = t.getParentSight()) != null) {
				if (p.isRegion()) return p;
				t = p;
				p = null;
			}
		} 
		return null;
	}
	public Sight() {	
	}
	
	public Sight(String id, String name, String type, Double latitude,
			Double longitude, String parentSightId) {
		super();
		this.id = id;
		this.name = name;
		this.type = type;
		this.latitude = latitude;
		this.longitude = longitude;
		this.parentSightId = parentSightId;
	}
	
	public Sight clone() {
        Sight newSight = new Sight();
        
        newSight.id = id;
        newSight.name = name;
        newSight.type = type;
        newSight.latitude = latitude;
        newSight.longitude = longitude;
        newSight.parentSightId = parentSightId;
        newSight.parentSight = parentSight;
        newSight.city = city;
        newSight.country = country;
        
        return newSight;
	}
	
	/**
	 * build up sight tree
	 */
	public static void buildUpSightTree(HashMap<String, Sight> idHash) {
		Iterator it = idHash.keySet().iterator();
		// loop for build parent sight
		while (it.hasNext()) {
			String id = (String) it.next();
			Sight sight = idHash.get(id);
			
			String parentSightId = sight.getParentSightId();
			if (idHash.containsKey(parentSightId)) {
				sight.setParentSight(idHash.get(parentSightId));
			}
		}
		
		// build city cache
		it = idHash.keySet().iterator();
		while (it.hasNext()) {
			String id = (String) it.next();
			Sight sight = idHash.get(id);
		
			Sight tmp = sight;
			while (tmp != null) {
				if ("城市".equals(tmp.getType())) {
					sight.setCity(tmp);
				} else if ("国家".equals(tmp.getType())) {
					sight.setCountry(tmp);
				}
				
				tmp = tmp.getParentSight();
			}
			
		}
	}
	
	private static double rad(double d) {
		return d * Math.PI / 180.0;
	}
	
	/**
	 *  Computes two sight's distance 
	 */
	public static double computDistance(double la1, double lo1, double la2, double lo2) {
		double radLa1 = rad(la1);
		double radLa2 = rad(la2);
		
		double a = radLa1 - radLa2;
		double b = rad(lo1) - rad(lo2);
		
		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
				+ Math.cos(radLa1) * Math.cos(radLa2)
				* Math.pow(Math.sin(b / 2), 2)));
		
		s = s * EARTH_RADIUS;
		s = Math.round(s * 10000) / 10000.0;
		
		return s;
	}

	public boolean isAncestor(Sight sight) {
		if (sight == null) {
			return false;
		}
		
		Sight tmpSight = this;
		while (tmpSight != null) {
			if (tmpSight.getId().equals(sight.getId())) {
				return true;
			}
			tmpSight = tmpSight.getParentSight();
		}
		
		return false;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getParentSightId() {
		return parentSightId;
	}

	public void setParentSightId(String parentSightId) {
		this.parentSightId = parentSightId;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	
	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public Sight getParentSight() {
		return this.parentSight;
	}
	
	public void setParentSight(Sight sight) {
		this.parentSight = sight;
	}
	
	public Sight getCity() {
		return city;
	}

	public void setCity(Sight city) {
		this.city = city;
	}

	public Sight getCountry() {
		return country;
	}

	public void setCountry(Sight country) {
		this.country = country;
	}

	public int getStartPos() {
		return startPos;
	}
	public void setStartPos(int startPos) {
		this.startPos = startPos;
	}
	public int getEndPos() {
		return endPos;
	}
	public void setEndPos(int endPos) {
		this.endPos = endPos;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("id: " + this.id + " name: " + name + " type: " + type + " address: (" + latitude + "," + longitude + ") " + "postion: " + startPos + "-" + endPos +  " parentSightId: " + parentSightId);
		if (city != null) {
			sb.append(" city: " + city.getName());
		}
		if (country != null) {
			sb.append(" country: " + country.getName());
		}
		return sb.toString();
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Sight other = (Sight) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	public boolean isSight() {
		return TYPE_SIGHT.equals(type);
	}
	
	public boolean isArea() {
		return TYPE_AREA.equals(type);
	}	
	
	public boolean isCity() {
		return TYPE_CITY.equals(type);
	}
	
	public boolean isSpot() {
		return TYPE_SPOT.equals(type);
	}
	
	public boolean isCountry() {
		return TYPE_COUNTRY.equals(type);
	}
	
	public boolean isRegion() {
		return TYPE_REGION.equals(type);
	}
	
	public boolean isProvince() {
		return TYPE_PROVINCE.equals(type);
	}
	
	public boolean isForeign() {
		return TYPE_FOREIGN.equals(type);
	}
	
	public boolean isContinent() {
		return TYPE_CONTINENT.equals(type);
	}
	
}
