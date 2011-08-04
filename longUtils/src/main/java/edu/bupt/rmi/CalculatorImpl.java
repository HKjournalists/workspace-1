package edu.bupt.rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class CalculatorImpl extends UnicastRemoteObject implements Calculator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3734267266995062303L;

	public CalculatorImpl() throws RemoteException {
		super();
	}

	public long add(long a, long b) throws RemoteException {
		return a + b;
	}

	public long div(long a, long b) throws RemoteException {
		return a / b;
	}

	public long mul(long a, long b) throws RemoteException {
		return a * b;
	}

	public long sub(long a, long b) throws RemoteException {
		return a - b;
	}

	public ComputingUnit getOne(String ip, int port) throws RemoteException {
		ComputingUnit computingUnit = new ComputingUnit(ip, port);
		computingUnit.setFtpPassword("123456");
		computingUnit.setFtpPort(21);
		computingUnit.setFtpUsername("user");
		return computingUnit;
	}

}
