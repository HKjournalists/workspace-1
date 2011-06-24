/*
 * @(#)Itineray.java	0.01 2010/3/18
 * 	
 * Copyright (c)  2010-2012 Qunar, Inc.
 * All rights reserved.
 */

package bupt.longlong.qunar.itineray.ie;

import java.util.List;
import java.util.ArrayList;

/**
 * A itineray, list of day trips'.
 *  
 * @version 	0.01 18 Mar 2010
 * @author 	xunxin
 */
public class Itineray {
	
	/** day count */
	private int dayTotal = 0;
	
	/** list of day trips' */
	private List<DayTrip> dayTrips = new ArrayList<DayTrip>();

	public int getDayTotal() {
		return dayTotal;
	}
	
	public void setDayTotal(int dayTotal) {
		this.dayTotal = dayTotal;
	}
	
	public List<DayTrip> getDayTrips() {
		return dayTrips;
	}

	public void setDayTrips(List<DayTrip> dayTrips) {
		this.dayTrips = dayTrips;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(">>>Itineray:\n");
		sb.append("DayTotal:" + this.dayTotal + "\n");
		sb.append("Daytrips: \n");
		for (int i = 0; i < dayTrips.size(); i++) {
			sb.append(dayTrips.get(i).toString());
		}
		
		return sb.toString();
	}
}
