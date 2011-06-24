package edu.bupt.rmi;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingException;

public class WarehouseClient {

	public static void main(String[] args) throws NamingException, RemoteException {
		Context namingContext = new InitialContext();
		System.out.println("RMI registry bindings: ");
		Enumeration<NameClassPair> e = namingContext.list("rmi://localhost/");
		while (e.hasMoreElements()) {
			System.out.println(e.nextElement().getName());
		}
		String url = "rmi://localhost:1099/central_warehouse";
		Warehouse centralWarehouse = (Warehouse) namingContext.lookup(url);

		String descr = "Blackwell Toaster";
		double price = centralWarehouse.getPrice(descr);
		System.out.println(descr + ": " + price);

		Product product = centralWarehouse.getProduct(new ArrayList<String>());
		System.out.println(product.getDescription() + ": " + product.getPrice());

		List<Product> products = centralWarehouse.getProducts();
		for (Product p : products) {
			System.out.println(p.getDescription() + ": " + p.getPrice());
		}
	}
}
