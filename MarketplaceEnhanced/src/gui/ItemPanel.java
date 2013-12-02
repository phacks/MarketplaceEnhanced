package gui;

import java.awt.Color;
import java.lang.String;
import java.rmi.RemoteException;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import logic.Item;
import logic.MyClient;

@SuppressWarnings("serial")
public class ItemPanel extends JPanel implements ActionListener{

	public Item item;
	Font fontName = new Font("Cambria", Font.BOLD,15);
	Font fontDescription = new Font("Cambria", Font.ITALIC,12);
	JButton remove = new JButton("Remove");
	JButton removeMarket = new JButton("Remove");
	JButton sell = new JButton("Sell");
	JButton buy = new JButton("Buy this item");
	public MainPanel mainPanel;
	public MyClient client;
	JPanel abovePanel = new JPanel();
	JPanel belowPanel = new JPanel();
	JLabel nameItem;
	JLabel descriptionItem;
	JLabel owner;
	JLabel price;
	public boolean isInMarketPlace;
	BoxLayout mainLayout = new BoxLayout(this,BoxLayout.Y_AXIS);
	BoxLayout above = new BoxLayout(abovePanel, BoxLayout.Y_AXIS);
	BoxLayout below = new BoxLayout(belowPanel, BoxLayout.X_AXIS);

	private MyItemsPanel myItemsPanel;
	private AvailableItemsPanel availableItemsPanel;

	private JPanel subpanel;

	public ItemPanel(MainPanel mainPanel,Item item,MyClient client,JPanel subpanel) throws RemoteException{
		this.mainPanel = mainPanel;
		this.item = item;
		this.client = client;
		this.subpanel = subpanel;

		nameItem = new JLabel(item.getName());
		descriptionItem = new JLabel(item.getDescription());
		nameItem.setFont(fontName);
		descriptionItem.setFont(fontDescription);
		nameItem.setAlignmentX(Component.CENTER_ALIGNMENT);
		descriptionItem.setAlignmentX(Component.CENTER_ALIGNMENT);
		owner = new JLabel("Owner : " + item.getOwner().getName());
		price = new JLabel(item.getPrice() + " SEK");

		this.setLayout(mainLayout);

		this.add(abovePanel);
		this.add(belowPanel);

		abovePanel.setLayout(above);
		belowPanel.setLayout(below);

		abovePanel.add(nameItem);
		abovePanel.add(descriptionItem);

		if (subpanel.getClass().toString().equals("class gui.AvailableItemsPanel")){
			abovePanel.add(owner);
			abovePanel.add(price);

			owner.setAlignmentX(Component.CENTER_ALIGNMENT);
			price.setAlignmentX(Component.CENTER_ALIGNMENT);

			if (client.getName().equals(item.getOwner().getName())){
				belowPanel.add(removeMarket);
				removeMarket.addActionListener(this);
				removeMarket.setAlignmentX(Component.CENTER_ALIGNMENT);
			} else if (!client.getName().equals(item.getOwner().getName())) {
				belowPanel.add(buy);
				buy.setAlignmentX(Component.CENTER_ALIGNMENT);
				buy.addActionListener(this);
			}
		} else if ((subpanel.getClass()).toString().equals("class gui.MyItemsPanel")){
			belowPanel.add(sell);
			belowPanel.add(remove);
			sell.addActionListener(this);
			remove.addActionListener(this);
			sell.setAlignmentX(Component.CENTER_ALIGNMENT);
			remove.setAlignmentX(Component.CENTER_ALIGNMENT);
		}

		this.setBorder(BorderFactory.createLineBorder(Color.black));

		if (item.getonSale() == true && (subpanel.getClass()).toString().equals("class gui.MyItemsPanel")){
			nameItem.setForeground(Color.GRAY);
			descriptionItem.setForeground(Color.GRAY);
			this.setBorder(BorderFactory.createLineBorder(Color.GRAY));
			sell.setEnabled(false);
			remove.setEnabled(false);
		} else if (item.getonSale() == false){
			nameItem.setForeground(Color.BLACK);
			descriptionItem.setForeground(Color.BLACK);
			this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			sell.setEnabled(true);
			remove.setEnabled(true);
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == sell){
			String itemName = item.getName();
			String itemDescription = item.getDescription();
			String price = (String)JOptionPane.showInputDialog(this,"You want to sell : " + itemName + " " + itemDescription + " at the following price : ","Sell an item",JOptionPane.PLAIN_MESSAGE,null,null,"");
			try {
				item.setPrice(price);
				item.setonSale(true);
				client.getServer().addItemToSell(item);
				if((subpanel.getClass()).toString().equals("class gui.MyItemsPanel"))
					subpanel.removeAll();
				((MyItemsPanel) subpanel).update();
				subpanel.repaint();
				subpanel.revalidate();
				client.getServer().callBackWish(item);
			} catch (RemoteException e1) {
				e1.printStackTrace();
			}
		} else if (e.getSource() == remove){
			client.removeItem(item);
			if ((subpanel.getClass()).toString().equals("class gui.MyItemsPanel")){
				subpanel.removeAll();
				try {
					((MyItemsPanel) subpanel).update();
				} catch (RemoteException e1) {
					e1.printStackTrace();
				}
				subpanel.repaint();
				subpanel.revalidate();
			}
		} else if (e.getSource() == buy){
			try {
				if (client.getServer().callBack(client, item.getOwner(),item)){
					client.getServer().removeItemToSell(item);
					item.setOwner(client);
					item.setonSale(false);
					client.addItem(item);

					if ((subpanel.getClass()).toString().equals("class gui.AvailableItemsPanel")){
						((AvailableItemsPanel) subpanel).removeAll();
						((AvailableItemsPanel) subpanel).update();
						((AvailableItemsPanel) subpanel).repaint();
						((AvailableItemsPanel) subpanel).revalidate();
					}
				}
			} catch (RemoteException e1) {
				e1.printStackTrace();
			}

		} else if (e.getSource() == removeMarket){
			try {
				client.getServer().removeItemToSell(item);
				client.removeItemMarket(item,item.getId());
				if ((subpanel.getClass()).toString().equals("class gui.AvailableItemsPanel")){
					((AvailableItemsPanel) subpanel).removeAll();
					((AvailableItemsPanel) subpanel).update();
					((AvailableItemsPanel) subpanel).repaint();
					((AvailableItemsPanel) subpanel).revalidate();
				}
			} catch (RemoteException e1) {
				e1.printStackTrace();
			}
		}
	}
}
