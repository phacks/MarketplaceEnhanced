package gui;

import java.awt.Color;
import java.lang.String;
import java.rmi.RemoteException;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import logic.ClientInterface;

import logic.Item;
import logic.MyClient;

@SuppressWarnings("serial")
public class ItemPanel extends JPanel implements ActionListener {

    public Item item;
    Font fontName = new Font("Cambria", Font.BOLD, 15);
    Font fontDescription = new Font("Cambria", Font.ITALIC, 12);
    JButton remove = new JButton("Remove");
    JButton removeMarket = new JButton("Remove");
    JButton sell = new JButton("Sell");
    JButton buy = new JButton("Buy this item");
    public MainPanel mainPanel;
    public MyClient client;
    public String name;
    public String owner;
    public String description;
    public String price;
    public String id;
    public String onSale;
    JPanel abovePanel = new JPanel();
    JPanel belowPanel = new JPanel();
    JLabel nameItemLabel;
    JLabel descriptionItemLabel;
    JLabel ownerLabel;
    JLabel priceLabel;
    public boolean isInMarketPlace;
    BoxLayout mainLayout = new BoxLayout(this, BoxLayout.Y_AXIS);
    BoxLayout above = new BoxLayout(abovePanel, BoxLayout.Y_AXIS);
    BoxLayout below = new BoxLayout(belowPanel, BoxLayout.X_AXIS);

    private MyItemsPanel myItemsPanel;
    private AvailableItemsPanel availableItemsPanel;

    private JPanel subpanel;

    public ItemPanel(MainPanel mainPanel, String name, String owner, String description, String price, String id, String onSale, MyClient client, JPanel subpanel) throws RemoteException {
        this.mainPanel = mainPanel;
        this.name = name;
        this.owner = owner;
        this.description = description;
        this.price = price;
        this.id = id;
        this.onSale = onSale;
        this.client = client;
        this.subpanel = subpanel;

        nameItemLabel = new JLabel(name);
        descriptionItemLabel = new JLabel(description);
        nameItemLabel.setFont(fontName);
        descriptionItemLabel.setFont(fontDescription);
        nameItemLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        descriptionItemLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        ownerLabel = new JLabel("Owner : " + owner);
        priceLabel = new JLabel(price + " SEK");

        this.setLayout(mainLayout);

        this.add(abovePanel);
        this.add(belowPanel);

        abovePanel.setLayout(above);
        belowPanel.setLayout(below);

        abovePanel.add(nameItemLabel);
        abovePanel.add(descriptionItemLabel);

        if (subpanel.getClass().toString().equals("class gui.AvailableItemsPanel")) {
            abovePanel.add(ownerLabel);
            abovePanel.add(priceLabel);

            ownerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            priceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            if (client.getName().equals(owner)) {
                belowPanel.add(removeMarket);
                removeMarket.addActionListener(this);
                removeMarket.setAlignmentX(Component.CENTER_ALIGNMENT);
            } else if (!client.getName().equals(owner)) {
                belowPanel.add(buy);
                buy.setAlignmentX(Component.CENTER_ALIGNMENT);
                buy.addActionListener(this);
            }
        } else if ((subpanel.getClass()).toString().equals("class gui.MyItemsPanel")) {
            belowPanel.add(sell);
            belowPanel.add(remove);
            sell.addActionListener(this);
            remove.addActionListener(this);
            sell.setAlignmentX(Component.CENTER_ALIGNMENT);
            remove.setAlignmentX(Component.CENTER_ALIGNMENT);
        }

        this.setBorder(BorderFactory.createLineBorder(Color.black));

    }
    
    public ItemPanel(MainPanel mainPanel, Item item, MyClient client, JPanel subpanel) throws RemoteException {
        this.mainPanel = mainPanel;
        this.item = item;
        this.client = client;
        this.subpanel = subpanel;

        nameItemLabel = new JLabel(item.getName());
        descriptionItemLabel = new JLabel(item.getDescription());
        nameItemLabel.setFont(fontName);
        descriptionItemLabel.setFont(fontDescription);
        nameItemLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        descriptionItemLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        ownerLabel = new JLabel("Owner : " + item.getOwner().getName());
        priceLabel = new JLabel(item.getPrice() + " SEK");

        this.setLayout(mainLayout);

        this.add(abovePanel);
        this.add(belowPanel);

        abovePanel.setLayout(above);
        belowPanel.setLayout(below);

        abovePanel.add(nameItemLabel);
        abovePanel.add(descriptionItemLabel);

        if (subpanel.getClass().toString().equals("class gui.AvailableItemsPanel")) {
            abovePanel.add(ownerLabel);
            abovePanel.add(priceLabel);

            ownerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            priceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            if (client.getName().equals(item.getOwner().getName())) {
                belowPanel.add(removeMarket);
                removeMarket.addActionListener(this);
                removeMarket.setAlignmentX(Component.CENTER_ALIGNMENT);
            } else if (!client.getName().equals(item.getOwner().getName())) {
                belowPanel.add(buy);
                buy.setAlignmentX(Component.CENTER_ALIGNMENT);
                buy.addActionListener(this);
            }
        } else if ((subpanel.getClass()).toString().equals("class gui.MyItemsPanel")) {
            belowPanel.add(sell);
            belowPanel.add(remove);
            sell.addActionListener(this);
            remove.addActionListener(this);
            sell.setAlignmentX(Component.CENTER_ALIGNMENT);
            remove.setAlignmentX(Component.CENTER_ALIGNMENT);
        }

        this.setBorder(BorderFactory.createLineBorder(Color.black));

        if (item.getonSale() == true && (subpanel.getClass()).toString().equals("class gui.MyItemsPanel")) {
            nameItemLabel.setForeground(Color.GRAY);
            descriptionItemLabel.setForeground(Color.GRAY);
            this.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            sell.setEnabled(false);
            remove.setEnabled(false);
        } else if (item.getonSale() == false) {
            nameItemLabel.setForeground(Color.BLACK);
            descriptionItemLabel.setForeground(Color.BLACK);
            this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            sell.setEnabled(true);
            remove.setEnabled(true);
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == sell) {
            String itemName = item.getName();
            String itemDescription = item.getDescription();
            String inputPrice = (String) JOptionPane.showInputDialog(this, "You want to sell : " + itemName + " " + itemDescription + " at the following price : ", "Sell an item", JOptionPane.PLAIN_MESSAGE, null, null, "");
            try {
                item.setPrice(inputPrice);
                item.setonSale(true);
                client.getServer().addItemToSell(item);
                if ((subpanel.getClass()).toString().equals("class gui.MyItemsPanel")) {
                    subpanel.removeAll();
                }
                ((MyItemsPanel) subpanel).update();
                subpanel.repaint();
                subpanel.revalidate();
                client.getServer().callBackWish(item);
            } catch (RemoteException e1) {
                e1.printStackTrace();
            }
        } else if (e.getSource() == remove) {
            client.removeItem(item);
            if ((subpanel.getClass()).toString().equals("class gui.MyItemsPanel")) {
                subpanel.removeAll();
                try {
                    ((MyItemsPanel) subpanel).update();
                } catch (RemoteException e1) {
                    e1.printStackTrace();
                }
                subpanel.repaint();
                subpanel.revalidate();
            }
        } else if (e.getSource() == buy) {
            try {
                if (client.getServer().checkAccount(client, price)){
                    client.getServer().updateLogTable(client, owner);
                    client.getServer().udpateAccounts(client, owner, price);
                    client.getServer().removeItemToSell(id);
                    Item item = new Item(name, client , description,"",false);
                    client.addItem(item);
                    client.getServer().callBack(client, owner, price, id);
                    if ((subpanel.getClass()).toString().equals("class gui.AvailableItemsPanel")) {
                        ((AvailableItemsPanel) subpanel).removeAll();
                        try {
                            ((AvailableItemsPanel) subpanel).update();
                        } catch (SQLException ex) {
                            Logger.getLogger(ItemPanel.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        ((AvailableItemsPanel) subpanel).repaint();
                        ((AvailableItemsPanel) subpanel).revalidate();
                    }
                }
            } catch (RemoteException e1) {
                e1.printStackTrace();
            }

        } else if (e.getSource() == removeMarket) {
            try {
                client.getServer().removeItemToSell(name);
                UUID uuid = UUID.fromString(id);
                System.out.println(uuid);
                client.removeItemMarket(uuid);
                if ((subpanel.getClass()).toString().equals("class gui.AvailableItemsPanel")) {
                    ((AvailableItemsPanel) subpanel).removeAll();
                    try {
                        ((AvailableItemsPanel) subpanel).update();
                    } catch (SQLException ex) {
                        Logger.getLogger(ItemPanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    ((AvailableItemsPanel) subpanel).repaint();
                    ((AvailableItemsPanel) subpanel).revalidate();
                }
            } catch (RemoteException e1) {
                e1.printStackTrace();
            }
        }
    }
}
