package gui;

import java.rmi.RemoteException;
import java.util.Iterator;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import logic.Item;
import logic.MyClient;

@SuppressWarnings("serial")
public class MyItemsPanel extends JPanel{

	BoxLayout bl = new BoxLayout(this, BoxLayout.Y_AXIS);
	public MainPanel mainPanel;
	MyClient client;
	Item item;

	public MyItemsPanel(MainPanel mainPanel, MyClient client){
		this.mainPanel = mainPanel;
		this.client = client;
		this.setLayout(bl);
	}

	public void update() throws RemoteException {
		Iterator<Item> it = client.getMyItemTable().iterator();
		while (it.hasNext()) {
			item = it.next();
			this.add(new ItemPanel(mainPanel, item,client,this));
		}
	}

}
