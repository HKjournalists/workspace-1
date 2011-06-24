package edu.bupt.rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WarehouseImpl extends UnicastRemoteObject implements Warehouse {

	private static final long serialVersionUID = -6998785212214304001L;

	public WarehouseImpl(Warehouse backup) throws RemoteException {
		products = new HashMap<String, Product>();
		this.backup = backup;
	}

	public void add(String keyword, Product product) {
		product.setLocation(this);
		products.put(keyword, product);
	}

	public double getPrice(String description) throws RemoteException {
		for (Product p : products.values()) {
			if (p.getDescription().equals(description)) return p.getPrice();
		}
		if (backup == null)
			return 0;
		else return backup.getPrice(description);
	}

	public Product getProduct(List<String> keywords) throws RemoteException {
		Product p = new Product("My product", 45.66);
		return p;
		//		for (String keyword : keywords) {
		//			Product p = products.get(keyword);
		//			if (p != null) return p;
		//		}
		//		if (backup != null)
		//			return backup.getProduct(keywords);
		//		else if (products.values().size() > 0)
		//			return products.values().iterator().next();
		//		else return null;
	}

	public List<Product> getProducts() throws RemoteException {
		Product p = new Product("My product1", 45.66);
		List<Product> products = new ArrayList<Product>();
		products.add(p);
		Product p2 = new Product("My product2", 34.20);
		products.add(p2);
		return products;
	}

	private Map<String, Product> products;
	private Warehouse backup;
}
