package logic;

import gui.Window;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@SuppressWarnings("serial")
public class MyClient extends UnicastRemoteObject implements ClientInterface {
	private String name;
	private ServerInterface server;
	private  List<Item> myItemTable = new ArrayList<Item>();
	private BankInterface bank;

	public MyClient(String name) throws RemoteException {
		super();	
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name){
		this.name = name;
	}

	public ServerInterface getServer() {
		return server;
	}

	public void setServer(ServerInterface server) {
		this.server = server;
	}

	// Connects to the server & the bank
	public boolean connectTo(String inputIP, String inputPortServer, String inputPortBank) {
		try {
			setBank((BankInterface) Naming.lookup("rmi://" + inputIP + ":" + inputPortBank + "/bank"));
			//System.out.println("Connection a la banque OK");
			setServer((ServerInterface) Naming.lookup("rmi://" + inputIP + ":" + inputPortServer + "/server"));
			System.out.println("Connection au serveur OK");
			return true;
		} catch (MalformedURLException | RemoteException | NotBoundException e ) {
			return false;
		}
	}

	private void setBank(BankInterface bank) throws RemoteException {
		this.bank = bank;
	}
	
	public BankInterface getBank() throws RemoteException {
		return bank;
	}

	public void addItem(Item item){
		getMyItemTable().add(item);
	}

	public void removeItem(Item item){
		if (getMyItemTable().contains(item)) {
			getMyItemTable().remove(item);
		}
		
	}

	public static void main(String args[]) throws NotBoundException, IOException {
		new Window(500,500);
	}

	public List<Item> getMyItemTable() {
		return myItemTable;
	}

	public void removeItemMarket(Item inputItem, int inputId){
		int id = inputItem.getId();
		Iterator<Item> it = getMyItemTable().iterator();
		Item item;
		while (it.hasNext()){
			item = it.next();
			if (item.getId()==id){
				item.setonSale(false);
				break;
			}
		}
	}

	public void setMyItemTable(List<Item> myItemTable) {
		this.myItemTable = myItemTable;
	}

	public boolean itemSold(Item item) {
		new PopUpThread("Item " + item.getName() +  " sold at the price : " + item.getPrice()).start(); 
		return true;
	}
	
	public void removeItemSold(Item inputItem,int inputId){
		int id = inputItem.getId();
		Iterator<Item> it = getMyItemTable().iterator();
		Item item;
		while (it.hasNext()){
			item = it.next();
			if (item.getId()==id){
				myItemTable.remove(item);
				break;
			}
		}
	}

	public void tooExpensive() {
		new PopUpThread("This item is too expensive").start();
	}
	
	public void wishAvailable(Item item){
		new PopUpThread("Item " + item.getName() + " available at the following price " + item.getPrice()).start();
	}

}