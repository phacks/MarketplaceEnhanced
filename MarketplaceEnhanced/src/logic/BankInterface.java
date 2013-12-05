package logic;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface BankInterface extends Remote {
	void registerClient(ClientInterface client) throws RemoteException;
	public void unregisterClient(ClientInterface client) throws RemoteException;
	List<ClientInterface> getClients() throws RemoteException;
	public void creditAccount(String owner, int sum) throws RemoteException;
	public void debitAccount(String buyer, int sum) throws RemoteException;
	public String checkAccount(String name) throws RemoteException;
}
