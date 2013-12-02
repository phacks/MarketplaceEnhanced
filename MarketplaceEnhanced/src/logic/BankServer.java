package logic;

import gui.ServerWindow;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

@SuppressWarnings("serial")
public class BankServer extends UnicastRemoteObject implements BankInterface {
	private List<ClientInterface> clientTable = new ArrayList<ClientInterface>();
	private List<Integer> accountsTable = new ArrayList<Integer>();
	private ClientInterface clientInterface;
	private MyClient client;

	public BankServer(String port, String serverValue) throws IOException, RemoteException {
		super();
		System.out.println("Bank console");
		LocateRegistry.createRegistry(Integer.parseInt(port));
		//String[] command = new String[]{"rmiregistry",port};
		//Runtime.getRuntime().exec(command);
		Naming.rebind("rmi://localhost:" + serverValue + "/bank", this);
	}

	public List<ClientInterface> getClients() {
		return(clientTable);
	}

	public void registerClient(ClientInterface client) throws RemoteException {
		if (clientTable.contains(client)) {
			throw new RemoteException("client already registered");
		}
		clientTable.add(client);
		accountsTable.add(clientTable.indexOf(client), 10000);
	}

	public void unregisterClient(ClientInterface client) throws RemoteException {

		if (!clientTable.contains(client))
		{
			throw new RemoteException("client not registered");
		}
		accountsTable.remove(clientTable.indexOf(client));
		clientTable.remove(client);
	}

	public static void main(String[] args) throws IOException {
		try {
			String portValue = JOptionPane.showInputDialog("Please input a port value");
			String serverValue = JOptionPane.showInputDialog("Please input the Server port value");
			new BankServer(portValue, serverValue);
			System.out.println("creation of the bank ok");
			new ServerWindow(400, 400, portValue, "Bank Server");
		} catch (RemoteException re) {
			System.out.println(re);
			System.exit(1);
		} catch (MalformedURLException me) {
			System.out.println(me);
			System.exit(1);
		}
	}

	public void creditAccount(ClientInterface client, int sum) throws RemoteException {
		if (!clientTable.contains(client))
		{
			throw new RemoteException("client not registered");
		}
		accountsTable.set(clientTable.indexOf(client), accountsTable.get(clientTable.indexOf(client)) + sum);
	}

	public void debitAccount(ClientInterface client, int sum) throws RemoteException {
		if (!clientTable.contains(client))
		{
			throw new RemoteException("client not registered");
		}
		accountsTable.set(clientTable.indexOf(client), accountsTable.get(clientTable.indexOf(client)) - sum);
	}

	public int checkAccount(ClientInterface client) {
		return accountsTable.get(clientTable.indexOf(client));
	}

}
