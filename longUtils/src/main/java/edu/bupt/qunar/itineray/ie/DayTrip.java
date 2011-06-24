/*
 * @(#)DayTrip.java	0.01 2010/3/18
 * 	
 * Copyright (c)  2010-2012 Qunar, Inc.
 * All rights reserved.
 */

package edu.bupt.qunar.itineray.ie;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A day trip, list of sights'.
 * 
 * @version 0.01 18 Mar 2010
 * @author xunxin
 */
public class DayTrip {

	/** a day trip's title */
	private String title;

	/** a day trip's title info */
	private String titleInfo;
	
	/** a day trip's description */
	private String description;

	/** the n's day */
	private int dayNum = 0;
	
	/** footer info */
	private String footer;

	/** list of sights' */
	private List<Sight> sights = new ArrayList<Sight>();

	private Sight fromCity;
	
	/** to regions */
	private ArrayList<Sight> toRegions = new ArrayList<Sight>();
	
	/** to sights */
	private ArrayList<Sight> toSights = new ArrayList<Sight>();
	
	/** need audit sights */
	private ArrayList<Sight> toAuditSights = new ArrayList<Sight>();
	
	private HashMap dedupToRegionHash = new HashMap();
	
	//从行程中取得的景点
	private List<OptionSight> optionSights = new ArrayList<OptionSight>();
	
	//未定的城市，需要结合整个上下文来判断
	private Map<Integer, OptionSight> citiesByTitle = new HashMap();
	
	
	public List<OptionSight> getOptionSights() {
		return optionSights;
	}

	public void setOptionSights(List<OptionSight> optionSights) {
		this.optionSights = optionSights;
	}

	//在根据描述判断城市的时候，需要结合整个上下文来判断
	private List citiesByDesc = new ArrayList();
	

	public List getCitiesByDesc() {
		return citiesByDesc;
	}

	public void setCitiesByDesc(List citiesByDesc) {
		this.citiesByDesc = citiesByDesc;
	}

	

	public Map<Integer, OptionSight> getCitiesByTitle() {
		return citiesByTitle;
	}

	public void setCitiesByTitle(Map<Integer, OptionSight> citiesByTitle) {
		this.citiesByTitle = citiesByTitle;
	}

	public DayTrip() {

	}

	public DayTrip(String title, String description, String titleInfo, int dayNum) {
		this.title = title;
		this.description = description;
		this.titleInfo = titleInfo;
		this.dayNum = dayNum;
	}

	public void addToRegions(Sight sight) {
		if (dedupToRegionHash.containsKey(sight.getId())) {
			
		} else {
			this.toRegions.add(sight);
			dedupToRegionHash.put(sight.getId(), "");
		}
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitleInfo() {
		return titleInfo;
	}

	public void setTitleInfo(String titleInfo) {
		this.titleInfo = titleInfo;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getDayNum() {
		return dayNum;
	}

	public void setDayNum(int dayNum) {
		this.dayNum = dayNum;
	}

	public String getFooter() {
		return footer;
	}

	public void setFooter(String footer) {
		this.footer = footer;
	}

	public List<Sight> getSights() {
		return sights;
	}

	public void setSights(List<Sight> sights) {
		this.sights = sights;
	}

	public Sight getFromCity() {
		return fromCity;
	}

	public void setFromCity(Sight fromCity) {
		this.fromCity = fromCity;
	}

	public ArrayList<Sight> getToRegions() {
		return toRegions;
	}

	public void setToRegions(ArrayList<Sight> toRegions) {
		this.toRegions = toRegions;
	}

	public ArrayList<Sight> getToSights() {
		return toSights;
	}

	public void setToSights(ArrayList<Sight> toSights) {
		this.toSights = toSights;
	}

	public ArrayList<Sight> getToAuditSights() {
		return toAuditSights;
	}

	public void setToAuditSights(ArrayList<Sight> toAuditSights) {
		this.toAuditSights = toAuditSights;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Title: " + this.title + "\n");
		sb.append("TitleInfo: " + this.titleInfo + "\n");
		sb.append("Description: " + this.description + "\n");
		sb.append("dayNum: " + this.dayNum + "\n");
		sb.append("Footer: " + this.footer + "\n");
		sb.append("From city: " + this.fromCity + "\n");
		sb.append("Sights: " + "\n");

		int size = sights.size();
		for (int i = 0; i < size; i++) {
			sb.append(sights.get(i).toString() + "\n");
		}

		sb.append("To Sights: " + "\n");
		size = toSights.size();
		for (int i = 0; i < size; i++) {
			sb.append(toSights.get(i).toString() + "\n");
		}
	
		sb.append("To Regions: " + "\n");
		size = toRegions.size();
		for (int i = 0; i < size; i++) {
			sb.append(toRegions.get(i).toString() + "\n");
		}
		
		sb.append("To Audit Sights: " + "\n");
		size = this.toAuditSights.size();
		for (int i = 0; i < size; i++) {
			sb.append(toAuditSights.get(i).toString() + "\n");
		}
	
		return sb.toString();
	}
}
