package gui;

import static com.sun.org.apache.regexp.internal.RETest.test;
import java.awt.Dimension;
import java.rmi.RemoteException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import logic.Item;
import logic.MyClient;

@SuppressWarnings("serial")
public class AvailableItemsPanel extends JPanel {

    BoxLayout bl = new BoxLayout(this, BoxLayout.Y_AXIS);
    public MainPanel mainPanel;
    String nameItem;
    String owner;
    String price;
    String description;
    MyClient client;
    Item item;
    ResultSet result;
    int size = 0;
    String[][] tab;

    public AvailableItemsPanel(MainPanel mainPanel, MyClient client) throws SQLException, RemoteException{
        this.mainPanel = mainPanel;
        this.client = client;
        this.setLayout(bl);
        this.update();
    }

    public void update() throws SQLException, RemoteException {
        try {
            size = client.getServer().getItemToSellTable();
            tab = client.getServer().readLineFromItemTable(size);
        } catch (RemoteException ex) {
            Logger.getLogger(AvailableItemsPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
            for (int i = 0;i<size;i++){                          
                this.add(new ItemPanel(mainPanel, tab[i][0], tab[i][1], tab[i][2], tab[i][3], tab[i][4], tab[i][5], client, this));
            }
    }

}
