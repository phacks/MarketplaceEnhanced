package gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.Iterator;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import logic.Item;
import logic.MyClient;
import logic.Wish;
import logic.WishInterface;

@SuppressWarnings("serial")
public class WishListPanel extends JPanel implements ActionListener {
	
	private MainPanel mainPanel;
	private JButton wishButton = new JButton("Make this wish");
	private JTextField nameItem = new JTextField("Name of the item you are looking for");
	private JTextField priceItem = new JTextField("Maximum price");
	MyClient client;
	MyItemsPanel itemPanel;
	private JPanel addWishPanel = new JPanel();
	private JPanel listWishPanel = new JPanel();
	BoxLayout addWishLayout = new BoxLayout(addWishPanel, BoxLayout.Y_AXIS);
	BoxLayout listWishLayout = new BoxLayout(listWishPanel, BoxLayout.Y_AXIS);
	Item item;
	WishInterface wishInterface;

	public WishListPanel(MainPanel mainPanel, MyClient client){
		this.mainPanel = mainPanel;
		this.client = client;
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		this.add(addWishPanel);
		this.add(listWishPanel);
		
		addWishPanel.setLayout(addWishLayout);
		listWishPanel.setLayout(listWishLayout);
		
		addWishPanel.add(nameItem);
		addWishPanel.add(priceItem);
		addWishPanel.add(wishButton);
		wishButton.addActionListener(this);
		nameItem.setAlignmentX(Component.CENTER_ALIGNMENT);
		priceItem.setAlignmentX(Component.CENTER_ALIGNMENT);
		wishButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		wishButton.setSize(200, 100);
		nameItem.setMaximumSize(new Dimension(Integer.MAX_VALUE,nameItem.getPreferredSize().height));
		priceItem.setMaximumSize(new Dimension(Integer.MAX_VALUE,priceItem.getPreferredSize().height));

	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == wishButton){
			String nameI = nameItem.getText();
			String priceI = priceItem.getText();
			Wish wish = null;
			try {
				wish = new Wish(nameI, client, priceI);
			} catch (RemoteException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			try {
				client.getServer().addWish(wish);
			} catch (RemoteException e1) {
				e1.printStackTrace();
			}
			JOptionPane.showMessageDialog(null,"You wish " + nameI + " at the following price : " + priceI + " SEK");
			try {
				Iterator<WishInterface> it = client.getServer().getWishTable().iterator();
				listWishPanel.repaint();
				listWishPanel.revalidate();
				while (it.hasNext()) {
					wishInterface = it.next();
					if (wish.getWisher().getName().equals(client.getName())) {
						this.add(new WishPanel(mainPanel, wish,client,listWishPanel));
						break;
					}
				}
			} catch (RemoteException r) {
				r.printStackTrace();
			}
			
		}	
	}

}
