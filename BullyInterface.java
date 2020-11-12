package com.java.bullyalgorithm;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface BullyInterface extends Remote {

	public String startElection(String nodeId) throws RemoteException;

	public String sendOk(String where, String to) throws RemoteException;

	public String iWon(String node) throws RemoteException;

	public boolean isalive() throws RemoteException;
}
