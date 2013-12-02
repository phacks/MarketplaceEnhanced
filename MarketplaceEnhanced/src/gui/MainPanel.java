package gui;

import java.awt.Color;
import java.rmi.RemoteException;

import javax.swing.JPanel;

import logic.MyClient;

@SuppressWarnings("serial")
public class MainPanel extends JPanel {

	public MainPanel(int width, int height){
		this.setSize(width, height);
		this.setBackground(Color.white);
		setConnection();
	}

	public void setRegister(MyClient client){
		RegisterPanel registerPanel = new RegisterPanel(this, client);
		this.removeAll();
		this.add(registerPanel);
		this.repaint();
		this.revalidate();
	}

	public void setConnection(){
		ConnectionPanel connectionPanel = new ConnectionPanel(this);
		this.removeAll();
		this.add(connectionPanel);
		this.repaint();
		this.revalidate();
	}

	public void setMarketplace(MyClient client, String name) throws RemoteException {
		MarketplacePanel marketplacePanel = new MarketplacePanel(this, client,name);
		this.removeAll();
		this.add(marketplacePanel);
		this.repaint();
		this.revalidate();		
	}
}