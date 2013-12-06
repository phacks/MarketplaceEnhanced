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
import java.util.UUID;

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

	public boolean connectTo(String inputIP, String inputPortServer, String inputPortBank) {
		try {
			setBank((BankInterface) Naming.lookup("rmi://" + inputIP + ":" + inputPortBank + "/bank"));

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

	public void removeItemMarket(UUID inputId){
		Iterator<Item> it = getMyItemTable().iterator();
		Item item;
		while (it.hasNext()){
                    System.out.println("Dans le while");
			item = it.next();
			if (item.getId().equals(inputId)){
                            System.out.println("Dans le if");
				item.setonSale(false);
				break;
			}
		}
	}

	public void setMyItemTable(List<Item> myItemTable) {
		this.myItemTable = myItemTable;
	}

	public boolean itemSold() {
		new PopUpThread("Item sold").start(); 
		return true;
	}
	
	public void removeItemSold(String inputId){
		Iterator<Item> it = getMyItemTable().iterator();
		Item item;
		while (it.hasNext()){
			item = it.next();
			if (((item.getId()).toString()).equals(inputId)){
				myItemTable.remove(item);
				break;
			}
		}
	}

	public void tooExpensive() {
		new PopUpThread("This item is too expensive").start();
	}
	
	public void wishAvailable(){
		new PopUpThread("One item you are looking for is available").start();
	}

        public void alreadyExistsPopUp(){
            new PopUpThread("This client already exists").start();
        }
        
        public void passwordInvalidPopUp(){
            new PopUpThread("Rejected : Password must be at least 8 characters long ").start();
        }
        
        public void registrationOKPopUp(){
            new PopUpThread("You are registered now, please sign in to continue").start();
        }
        
        public void wrongPasswordPopUp(){
            new PopUpThread("Wrong password").start();
        }
        
        public void clientNotRegisteredPopUp(){
            new PopUpThread("This client is not registered").start();
        }
        
        public void wishAlreadyExistsPopUp(){
            new PopUpThread("You have already made this wish").start();
        }
        
        public void wishRegistered(){
            new PopUpThread("Your wish is registered").start();
        }
}