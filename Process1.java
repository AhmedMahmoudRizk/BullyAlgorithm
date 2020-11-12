package com.java.bullyalgorithm;

import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Process1 implements BullyInterface {
	boolean foundgreater = false;
	static boolean electionInProgress = false;
	private static String thisProcess = "1";
	private static String coordinator;
	static BullyInterface stub2;

	public Process1() {

	}

	public static void main(String[] args) {
		Process1 obj = new Process1();

		try {

			stub2 = (BullyInterface) UnicastRemoteObject.exportObject(obj, 0);
			// Bind the remote object's stub in the registry
			Registry registry = LocateRegistry.getRegistry();
			registry.bind(thisProcess, stub2);

			System.err.println("Process" + thisProcess + " is  ready");
			stub2.startElection(thisProcess);
		} catch (RemoteException e) {
			System.out.println("Couldnt bind Process to registry\n");
			e.printStackTrace();
		} catch (AlreadyBoundException e) {
			System.out.println("Process already bound to registry \n");
			e.printStackTrace();
		}
		Runtime.getRuntime().addShutdownHook(new ShutDown());
		repeat();

	}

	public static void repeat() {
		Random rand = new Random();
		int randomNum = rand.nextInt((6 - 1) + 1) + 1;
		Timer timer = new Timer();
		timer.schedule(new TimerCheck(), randomNum * 1000);
	}

	@Override
	public String startElection(String ProcessId) throws RemoteException {
		electionInProgress = true;
		foundgreater = false;
		//checking if the running process runs as a server or client

		if (ProcessId.equals(thisProcess)) {
			System.out.println("You started the elections");

			Registry reg = LocateRegistry.getRegistry();
			for (String ProcessName : reg.list()) {
				if (!ProcessName.equals(thisProcess) && Integer.parseInt(ProcessName) > 1) {
					System.out.println(reg.list().length);
					BullyInterface stub;
					try {
						stub = (BullyInterface) reg.lookup(ProcessName);
						System.out.println("Sending election challenge to " + ProcessName);
						stub.startElection(ProcessId);
						foundgreater = true;

					} catch (NotBoundException e) {
						e.printStackTrace();
					}
				}
			}
			if (!foundgreater) {
				iWon(thisProcess);
			}

			return null;
		} else {
			System.out.println("Received election request from " + ProcessId);
			sendOk(thisProcess, ProcessId);
			return null;
		}
	}

	@Override
	public String sendOk(String where, String to) throws RemoteException {
		if (!thisProcess.equals(to)) {
			try {
				Registry reg = LocateRegistry.getRegistry();
				BullyInterface stub = (BullyInterface) reg.lookup(to);
				System.out.println("Sending OK to " + to);
				stub.sendOk(where, to);

				// start election after sending OK
				startElection(thisProcess);
			} catch (NotBoundException e) {
				e.printStackTrace();
			}
		} else {
			// receive OK
			System.out.println(where + " Replied with Ok..");
		}
		return null;
	}

	@Override
	public String iWon(String Process) throws RemoteException {
		coordinator = Process;
		electionInProgress = false;
		if (Process.equals(thisProcess)) {
			// send win
			System.out.println("You have won the election.");
			System.out.println("Bragging about winning to other Processs.....");
			Registry reg = LocateRegistry.getRegistry();
			for (String ProcessName : reg.list()) {
				if (!ProcessName.equals(thisProcess)) {
					BullyInterface stub;
					try {
						stub = (BullyInterface) reg.lookup(ProcessName);
						stub.iWon(Process);

					} catch (NotBoundException e) {
						e.printStackTrace();
					}
				}
			}

			System.out.println("Process " + Process + " is the new Coodinator\n");
		} else {
			// receive win
			System.out.println("Process " + Process + " has won the election.");
			System.out.println("Process " + Process + " is the new Coodinator\n");
		}
		return null;
	}

	static class TimerCheck extends TimerTask {

		@Override
		public void run() {
			if (!thisProcess.equals(coordinator) && !electionInProgress) {
				try {
					Registry reg = LocateRegistry.getRegistry();
					BullyInterface stub;
					stub = (BullyInterface) reg.lookup(coordinator);
					stub.isalive();

				} catch (RemoteException e) {
					System.out.println("Error : remote exception");
				} catch (NotBoundException e) {
					coordinatorCrashed();
				}
			}
			repeat();

		}
	}

	private static void coordinatorCrashed() {
		System.out.println("Coordinator has crushed. Iniating new election");
		try {
			stub2.startElection(thisProcess);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	@Override
	public boolean isalive() throws RemoteException {
		return true;
	}

	static class ShutDown extends Thread {
		@Override
		public void run() {
			super.run();
			try {
				System.out.println("Terminating Process");
				LocateRegistry.getRegistry().unbind(thisProcess);
			} catch (AccessException e) {
				e.printStackTrace();
			} catch (RemoteException e) {
				e.printStackTrace();
			} catch (NotBoundException e) {
				e.printStackTrace();
			}
		}

	}
}
