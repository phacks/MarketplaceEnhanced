package logic;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.UUID;

public interface ClientInterface extends Remote {
	String getName() throws RemoteException;
	public ServerInterface getServer() throws RemoteException;
	void removeItem(Item item) throws RemoteException;
	public BankInterface getBank() throws RemoteException;
	List<Item> getMyItemTable() throws RemoteException;
	public void setName(String name) throws RemoteException;
	boolean itemSold() throws RemoteException;
	public void removeItemSold(String inputId) throws RemoteException;
	void tooExpensive() throws RemoteException;
	void wishAvailable() throws RemoteException;
        void alreadyExistsPopUp() throws RemoteException;
        void passwordInvalidPopUp() throws RemoteException;
        void registrationOKPopUp() throws RemoteException;
        void wrongPasswordPopUp() throws RemoteException;
        void clientNotRegisteredPopUp() throws RemoteException;
        void wishAlreadyExistsPopUp() throws RemoteException;
        void wishRegistered() throws RemoteException;
}