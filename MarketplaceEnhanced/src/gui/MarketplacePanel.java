package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    private JButton logoutButton = new JButton("Log out");
    private JPanel southPanel = new JPanel();
    private MyClient client;
    String name;
    MyItemsPanel itemsPanel;
    AvailableItemsPanel availableItemsPanel;
    LogPanel logPanel;
    private JLabel account = new JLabel("");

    public MarketplacePanel(MainPanel mainPanel, final MyClient client, String name) throws RemoteException, SQLException {
        this.mainPanel = mainPanel;
        this.client = client;
        this.name = name;

        itemsPanel = new MyItemsPanel(mainPanel, client);
        availableItemsPanel = new AvailableItemsPanel(mainPanel, client);
        logPanel = new LogPanel(client);
        this.setPreferredSize(new Dimension(mainPanel.getWidth() - 50, mainPanel.getHeight() - 50));

        this.setLayout(new BorderLayout());

        southPanel.setLayout(new FlowLayout());

        this.add(southPanel, BorderLayout.SOUTH);
        southPanel.add(logoutButton);
        southPanel.add(unregisterButton);
       

        account.setText(client.getBank().checkAccount(client.getName()) + " SEK");
        southPanel.add(account);

        setMenu();
        this.add(menu, BorderLayout.CENTER);
        logoutButton.addActionListener(this);
        unregisterButton.addActionListener(this);

        menu.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (menu.getSelectedIndex() == 1) {
                    itemsPanel.removeAll();
                    try {
                        itemsPanel.update();
                    } catch (RemoteException e1) {
                        e1.printStackTrace();
                    }
                    itemsPanel.repaint();
                    itemsPanel.revalidate();
                    try {
                        account.setText(client.getBank().checkAccount(client.getName()) + " SEK");
                    } catch (RemoteException e1) {
                        e1.printStackTrace();
                    }
                } else if (menu.getSelectedIndex() == 0) {
                    availableItemsPanel.removeAll();
                    try {
                        availableItemsPanel.update();
                    } catch (SQLException ex) {
                        Logger.getLogger(MarketplacePanel.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (RemoteException ex) {
                        Logger.getLogger(MarketplacePanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    availableItemsPanel.repaint();
                    availableItemsPanel.revalidate();
                    try {
                        account.setText(client.getBank().checkAccount(client.getName()) + " SEK");
                    } catch (RemoteException e1) {
                        e1.printStackTrace();
                    }
                } else if (menu.getSelectedIndex() == 2) {
                    try {
                        account.setText(client.getBank().checkAccount(client.getName()) + " SEK");
                    } catch (RemoteException e1) {
                        e1.printStackTrace();
                    }
                } else if (menu.getSelectedIndex() == 4){
                    try {
                        logPanel.update();
                        logPanel.repaint();
                        logPanel.revalidate();
                    } catch (RemoteException ex) {
                        Logger.getLogger(MarketplacePanel.class.getName()).log(Level.SEVERE, null, ex);
                    }                
                }
                    
            }
        });
    }

    private void setMenu() throws RemoteException {
        menu.addTab("Marketplace", availableItemsPanel);
        menu.addTab("My Items", itemsPanel);
        menu.addTab("Add Items", new AddItemsPanel(mainPanel, client));
        menu.addTab("Wishlist", new WishListPanel(mainPanel, client));
        menu.addTab("My log", logPanel);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == unregisterButton) {
            try {
                client.getServer().unregisterClient(client);
                client.getBank().unregisterClient(client);
                List<ClientInterface> clientTable = client.getServer().getClients();
                if (!clientTable.contains(name)) {
                    mainPanel.setConnection();
                }
            } catch (RemoteException e1) {
                e1.printStackTrace();
            }
        } else if (e.getSource() == logoutButton) {
            try {
                client.getServer().logOutClient(client);
            } catch (RemoteException ex) {
                Logger.getLogger(MarketplacePanel.class.getName()).log(Level.SEVERE, null, ex);
            }
            mainPanel.setRegister(client);
        }
    }

}
