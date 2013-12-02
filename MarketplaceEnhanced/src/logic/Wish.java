package logic;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

@SuppressWarnings("serial")
public class Wish extends UnicastRemoteObject implements Serializable, WishInterface {
	private String nameItem;
	private ClientInterface wisher;
	private String priceItem;
	
	public Wish(String nameItem, ClientInterface wisher, String priceItem) throws RemoteException {
		this.setNameItem(nameItem);
		this.setWisher(wisher);
		this.setPriceItem(priceItem);
	}

	public String getNameItem() {
		return nameItem;
	}

	public void setNameItem(String nameItem) {
		this.nameItem = nameItem;
	}

	public ClientInterface getWisher() {
		return wisher;
	}

	public void setWisher(ClientInterface wisher) {
		this.wisher = wisher;
	}

	public String getPriceItem() {
		return priceItem;
	}

	public void setPriceItem(String priceItem) {
		this.priceItem = priceItem;
	}

}
