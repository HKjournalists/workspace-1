package edu.bupt.rmi;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class WarehouseServer {

	public static void main(String[] args) throws RemoteException, NamingException {
		System.out.println("Constructing server implementaion...");
		WarehouseImpl backupWarehouse = new WarehouseImpl(null);
		WarehouseImpl centralWarehouse = new WarehouseImpl(backupWarehouse);

		centralWarehouse.add("toaster", new Product("Blackwell Toaster", 23.95));
		backupWarehouse.add("java", new Book("Core Java vol. 2", "01233246", 44.95));

		System.out.println("Binding server implementation to registry...");
		LocateRegistry.createRegistry(1099);
		Context namingContext = new InitialContext();
		namingContext.rebind("rmi:central_warehouse", centralWarehouse);

		System.out.println("Waiting for invocations from clients...");
	}
}
