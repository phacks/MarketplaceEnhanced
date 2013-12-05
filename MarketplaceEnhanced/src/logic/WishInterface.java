package logic;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.UUID;

public interface WishInterface extends Remote {

	public String getNameItem() throws RemoteException;
	public void setNameItem(String nameItem) throws RemoteException;
	public ClientInterface getWisher() throws RemoteException;
	public void setWisher(ClientInterface wisher) throws RemoteException;
	public String getPriceItem() throws RemoteException;
	public void setPriceItem(String priceItem) throws RemoteException;
        public UUID getUUID() throws RemoteException;
	public void setUUID(UUID uuid) throws RemoteException;
	
}