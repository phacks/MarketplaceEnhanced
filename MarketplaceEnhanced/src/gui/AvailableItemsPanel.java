package gui;

import java.rmi.RemoteException;
import java.util.Iterator;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import logic.Item;
import logic.MyClient;

@SuppressWarnings("serial")
public class AvailableItemsPanel extends JPanel {

	BoxLayout bl = new BoxLayout(this, BoxLayout.Y_AXIS);
	public MainPanel mainPanel;
	MyClient client;
	Item item;

	public AvailableItemsPanel(MainPanel mainPanel,MyClient client){
		this.mainPanel = mainPanel;
		this.client = client;
		this.setLayout(bl);
		this.update();
	}

	public void update() {
		try {
			Iterator<Item> it = client.getServer().getItemToSellTable().iterator();
			while (it.hasNext()) {
				item = it.next();
				this.add(new ItemPanel(mainPanel, item,client,this));
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

}
