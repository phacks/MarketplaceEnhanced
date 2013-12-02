package logic;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ClientInterface extends Remote {
	String getName() throws RemoteException;
	public ServerInterface getServer() throws RemoteException;
	void removeItem(Item item) throws RemoteException;
	public BankInterface getBank() throws RemoteException;
	List<Item> getMyItemTable() throws RemoteException;
	public void setName(String name) throws RemoteException;
	boolean itemSold(Item item) throws RemoteException;
	void removeItemSold(Item item,int id) throws RemoteException;
	void tooExpensive() throws RemoteException;
	void wishAvailable(Item item) throws RemoteException;
}