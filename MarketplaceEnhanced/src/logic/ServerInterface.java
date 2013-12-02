package logic;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ServerInterface extends Remote {
	void registerClient(ClientInterface client) throws RemoteException;
	public void unregisterClient(ClientInterface client) throws RemoteException;
	List<ClientInterface> getClients() throws RemoteException;
	void addItemToSell(Item item) throws RemoteException;
	void removeItemToSell(Item item) throws RemoteException;
	List<Item> getItemToSellTable() throws RemoteException;
	public ClientInterface getClient() throws RemoteException;
	public boolean callBack(ClientInterface buyer, ClientInterface owner, Item item) throws RemoteException;
	void setBank(BankInterface bank) throws RemoteException;
	void addWish(WishInterface wish) throws RemoteException;
	List<WishInterface> getWishTable() throws RemoteException;
	void removeWish(WishInterface wish) throws RemoteException;
	void callBackWish(Item item) throws RemoteException;
}