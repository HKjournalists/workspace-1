package edu.bupt.rmi;

import java.io.Serializable;

public class Product extends Thing implements Serializable {

	private static final long serialVersionUID = -7165273059629366328L;

	public Product(String description, double price) {
		this.description = description;
		this.price = price;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public Warehouse getLocation() {
		return location;
	}

	public void setLocation(Warehouse location) {
		this.location = location;
	}

	private String description;
	private double price;
	private Warehouse location;
}
