package edu.bupt.qunar.deals.util;

import java.util.List;

public class ShipDetail {

	private String shipName;
	
	private String company;
	
	private String voyageRegion;
	
	private String fromHub;
	
	private String toHub;
	
	private String priceHtml;
	
	private List<String> dates;
	
	private String voyageImg;
	
	private String voyageImage_snapShot_Origin;
	private String voyageImage_snapShot_Small;
	private String voyageImage_snapShot_Mid;
	private String voyageImage_snapShot_Big;
	
	public String getVoyageImage_snapShot_Small() {
		return voyageImage_snapShot_Small;
	}

	public void setVoyageImage_snapShot_Small(String voyageImageSnapShotSmall) {
		voyageImage_snapShot_Small = voyageImageSnapShotSmall;
	}

	public String getVoyageImage_snapShot_Mid() {
		return voyageImage_snapShot_Mid;
	}

	public void setVoyageImage_snapShot_Mid(String voyageImageSnapShotMid) {
		voyageImage_snapShot_Mid = voyageImageSnapShotMid;
	}

	public String getVoyageImage_snapShot_Big() {
		return voyageImage_snapShot_Big;
	}

	public void setVoyageImage_snapShot_Big(String voyageImageSnapShotBig) {
		voyageImage_snapShot_Big = voyageImageSnapShotBig;
	}

	private String cities;	

	public String getCities() {
		return cities;
	}

	public void setCities(String cities) {
		this.cities = cities;
	}

	public String getVoyageImage_snapShot_Origin() {
		return voyageImage_snapShot_Origin;
	}

	public void setVoyageImage_snapShot_Origin(String voyageImageSnapShotOrigin) {
		voyageImage_snapShot_Origin = voyageImageSnapShotOrigin;
	}

	public String getShipName() {
		return shipName;
	}

	public void setShipName(String shipName) {
		this.shipName = shipName;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getVoyageRegion() {
		return voyageRegion;
	}

	public void setVoyageRegion(String voyageRegion) {
		this.voyageRegion = voyageRegion;
	}

	public String getFromHub() {
		return fromHub;
	}

	public void setFromHub(String fromHub) {
		this.fromHub = fromHub;
	}

	public String getToHub() {
		return toHub;
	}

	public void setToHub(String toHub) {
		this.toHub = toHub;
	}

	public List<String> getDates() {
		return dates;
	}

	public void setDates(List<String> dates) {
		this.dates = dates;
	}

	public String getVoyageImg() {
		return voyageImg;
	}

	public void setVoyageImg(String voyageImg) {
		this.voyageImg = voyageImg;
	}

	
	public String getPriceHtml() {
		return priceHtml;
	}

	public void setPriceHtml(String priceHtml) {
		this.priceHtml = priceHtml;
	}

	@Override
	public String toString() {
		return "ShipDetail [company=" + company + ", dates=" + dates
				+ ", fromHub=" + fromHub + ", shipName=" + shipName
				+ ", toHub=" + toHub + ", voyageImg=" + voyageImg
				+ ", voyageRegion=" + voyageRegion + "]";
	}
	
	
}
