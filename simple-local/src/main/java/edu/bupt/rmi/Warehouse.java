package edu.bupt.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface Warehouse extends Remote {

	double getPrice(String description) throws RemoteException;

	Product getProduct(List<String> keywords) throws RemoteException;

	List<Product> getProducts() throws RemoteException;
}
