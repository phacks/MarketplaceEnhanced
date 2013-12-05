package gui;

import java.rmi.RemoteException;
import java.util.Iterator;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import logic.Item;
import logic.MyClient;

class LogPanel extends JPanel {

    MyClient client;
    JPanel abovePanel = new JPanel();
    JPanel belowPanel = new JPanel();
    BoxLayout mainLayout = new BoxLayout(this, BoxLayout.Y_AXIS);
    BoxLayout above = new BoxLayout(abovePanel, BoxLayout.X_AXIS);
    BoxLayout below = new BoxLayout(belowPanel, BoxLayout.X_AXIS);
    JLabel itemsSoldLabel = new JLabel("Items sold : ");
    JLabel itemsBoughtLabel = new JLabel("Items bought : ");
    int[] tab;

    public LogPanel(MyClient client) throws RemoteException {

        this.client = client;

        tab = client.getServer().getLog(client);
        itemsSoldLabel.setText("Items sold : " + Integer.toString(tab[0]));
        itemsBoughtLabel.setText("Items bought : " + Integer.toString(tab[1]));
        this.setLayout(mainLayout);

        this.add(abovePanel);
        this.add(belowPanel);

        abovePanel.setLayout(above);
        belowPanel.setLayout(below);

        abovePanel.add(itemsSoldLabel);
        belowPanel.add(itemsBoughtLabel);

    }

    public void update() throws RemoteException {
        tab = client.getServer().getLog(client);
        itemsSoldLabel.setText("Items sold : " + Integer.toString(tab[0]));
        itemsBoughtLabel.setText("Items bought : " + Integer.toString(tab[1]));
    }

}
