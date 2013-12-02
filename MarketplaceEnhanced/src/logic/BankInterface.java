package logic;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface BankInterface extends Remote {
	void registerClient(ClientInterface client) throws RemoteException;
	public void unregisterClient(ClientInterface client) throws RemoteException;
	List<ClientInterface> getClients() throws RemoteException;
	public void creditAccount(ClientInterface client, int sum) throws RemoteException;
	public void debitAccount(ClientInterface client, int sum) throws RemoteException;
	public int checkAccount(ClientInterface client) throws RemoteException;
}
