package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import logic.ClientInterface;
import logic.MyClient;

@SuppressWarnings("serial")
public class MarketplacePanel extends JPanel implements ActionListener {
	private JTabbedPane menu = new JTabbedPane();
	private MainPanel mainPanel;
	private JButton unregisterButton = new JButton("Unregister");
	private JPanel southPanel = new JPanel();
	private MyClient client;
	String name;
	MyItemsPanel itemsPanel;
	AvailableItemsPanel availableItemsPanel;
	private JLabel account = new JLabel("");

	public MarketplacePanel (MainPanel mainPanel, final MyClient client, String name) throws RemoteException{
		this.mainPanel = mainPanel;
		this.client = client;
		this.name = name;

		itemsPanel = new MyItemsPanel(mainPanel, client);
		availableItemsPanel = new AvailableItemsPanel(mainPanel, client);
		this.setPreferredSize(new Dimension(mainPanel.getWidth() - 50, mainPanel.getHeight() - 50));

		this.setLayout(new BorderLayout());

		southPanel.setLayout(new FlowLayout());

		this.add(southPanel, BorderLayout.SOUTH);
		southPanel.add(unregisterButton);
		
		account.setText(Integer.toString(client.getBank().checkAccount(client)) + " SEK");
		southPanel.add(account);

		setMenu();
		this.add(menu, BorderLayout.CENTER);
		unregisterButton.addActionListener(this);


		menu.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if(menu.getSelectedIndex() == 1){
					itemsPanel.removeAll();
					try {
						itemsPanel.update();
					} catch (RemoteException e1) {
						e1.printStackTrace();
					}
					itemsPanel.repaint();
					itemsPanel.revalidate();
					try {
						account.setText(Integer.toString(client.getBank().checkAccount(client)) + " SEK");
					} catch (RemoteException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				} else if (menu.getSelectedIndex() == 0){
					availableItemsPanel.removeAll();
					availableItemsPanel.update();
					availableItemsPanel.repaint();
					availableItemsPanel.revalidate();
					try {
						account.setText(Integer.toString(client.getBank().checkAccount(client)) + " SEK");
					} catch (RemoteException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				} else if (menu.getSelectedIndex() == 2){
					try {
						account.setText(Integer.toString(client.getBank().checkAccount(client)) + " SEK");
					} catch (RemoteException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
	}

	private void setMenu() {
		menu.addTab("Marketplace", availableItemsPanel);
		menu.addTab("My Items", itemsPanel);
		menu.addTab("Add Items", new AddItemsPanel(mainPanel,client));
		menu.addTab("Wishlist", new WishListPanel(mainPanel,client));
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == unregisterButton){
			try {
				client.getServer().unregisterClient(client);
				List<ClientInterface> clientTable = client.getServer().getClients();
				if (!clientTable.contains(name)){
					mainPanel.setConnection();
				}
			} catch (RemoteException e1) {
				e1.printStackTrace();
			}
		}
	}

}
