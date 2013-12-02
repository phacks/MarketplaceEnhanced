package gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.Iterator;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import logic.MyClient;
import logic.Wish;
import logic.WishInterface;

public class WishPanel extends JPanel implements ActionListener {

	JPanel wishListPanel;
	private JButton removeWish = new JButton("Remove this wish");
	private MainPanel mainPanel;
	private Wish wish;
	private MyClient client;
	private JPanel wishList;
	WishInterface wishInterface;
	


	public WishPanel(MainPanel mainPanel,WishInterface wishInterface,MyClient client,JPanel wishList) throws RemoteException{
		this.mainPanel = mainPanel;
		this.wishInterface = wishInterface;
		this.client = client;
		this.wishList = wishList;
		BoxLayout listWishLayout = new BoxLayout(wishList, BoxLayout.Y_AXIS);
		wishList.setLayout(listWishLayout);
		JLabel nameItem = new JLabel(wishInterface.getNameItem());
		JLabel priceItem = new JLabel(wishInterface.getPriceItem() + " SEK");
		wishList.add(nameItem);
		wishList.add(priceItem);
		wishList.add(removeWish);
		removeWish.addActionListener(this);
		nameItem.setAlignmentX(Component.CENTER_ALIGNMENT);
		priceItem.setAlignmentX(Component.CENTER_ALIGNMENT);
		removeWish.setAlignmentX(Component.CENTER_ALIGNMENT);
	}



	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == removeWish){
			try {
				client.getServer().removeWish(wishInterface);
				wishList.removeAll();
				Iterator<WishInterface> it = client.getServer().getWishTable().iterator();
				
				while (it.hasNext()) {
					wishInterface = it.next();
						this.add(new WishPanel(mainPanel, wishInterface,client,wishList));
				}
				wishList.repaint();
				wishList.revalidate();
			} catch (RemoteException e1) {
				e1.printStackTrace();
			}
		}
		
	}

}
