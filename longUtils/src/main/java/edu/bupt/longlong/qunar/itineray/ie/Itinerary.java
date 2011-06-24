package edu.bupt.longlong.qunar.itineray.ie;

import java.util.ArrayList;
import java.util.List;

public class Itinerary {

	private int day;
	
	private String fromCity;
	
	private String toCity;
	
	private String description;
	
	private String title;
	
	private List<Sight> toCities = new ArrayList();
	private List<Sight> fromCities = new ArrayList();
	
	private String breakfast; //早餐
	private String lunch;  //午餐
	private String dinner; //晚餐
	private String accommodation;  //住宿
 	private String transport;  //交通
	
	
	

	public String getBreakfast() {
		return breakfast;
	}

	public void setBreakfast(String breakfast) {
		this.breakfast = breakfast;
	}

	public String getLunch() {
		return lunch;
	}

	public void setLunch(String lunch) {
		this.lunch = lunch;
	}

	public String getDinner() {
		return dinner;
	}

	public void setDinner(String dinner) {
		this.dinner = dinner;
	}

	public String getAccommodation() {
		return accommodation;
	}

	public void setAccommodation(String accommodation) {
		this.accommodation = accommodation;
	}

	public String getTransport() {
		return transport;
	}

	public void setTransport(String transport) {
		this.transport = transport;
	}

	public List<Sight> getFromCities() {
		return fromCities;
	}

	public void setFromCities(List<Sight> fromCities) {
		this.fromCities = fromCities;
	}

	public List<Sight> getToCities() {
		return toCities;
	}

	public void setToCities(List<Sight> toCities) {
		this.toCities = toCities;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public String getFromCity() {
		return fromCity;
	}

	public void setFromCity(String fromCity) {
		this.fromCity = fromCity;
	}

	public String getToCity() {
		return toCity;
	}

	public void setToCity(String toCity) {
		this.toCity = toCity;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "Itinerary [day=" + day + ", description=" + description
				+ ", fromCity=" + fromCity + ", title=" + title + ", toCity="
				+ toCity + "]";
	}

	
	
}
