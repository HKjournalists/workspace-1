package com.qunar.deals.util;

public class ShipPrice {

	private String date;
	
	private String roomType;
	
	private String floor;
	
	private String price;
	
	private String desc;

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getRoomType() {
		return roomType;
	}

	public void setRoomType(String roomType) {
		this.roomType = roomType;
	}

	public String getFloor() {
		return floor;
	}

	public void setFloor(String floor) {
		this.floor = floor;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	@Override
	public String toString() {
		return "ShipPrice [date=" + date + ", desc=" + desc + ", floor="
				+ floor + ", price=" + price + ", roomType=" + roomType + "]";
	}
	
	
}
