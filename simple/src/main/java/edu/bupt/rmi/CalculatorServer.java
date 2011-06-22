package edu.bupt.rmi;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;


public class CalculatorServer {

	public CalculatorServer() {
		try {
			Calculator c = new CalculatorImpl();
			LocateRegistry.createRegistry(1099);
			Naming.rebind("rmi://localhost:1099/CalculatorService", c);
			System.out.print("Ready...");
		} catch (Exception e) {
			System.out.println("Trouble: " + e);
		}
	}

	public static void main(String args[]) {
		new CalculatorServer();
	}
}
