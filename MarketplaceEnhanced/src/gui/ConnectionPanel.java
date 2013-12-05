package gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import logic.MyClient;

@SuppressWarnings("serial")
public class ConnectionPanel extends JPanel implements ActionListener {
	
	private MainPanel mainPanel;
	private JButton connectionButton = new JButton("Connection");
	JTextField IPaddress = new JTextField("localhost");
	JTextField serverPort = new JTextField("14000");
	JTextField bankPort = new JTextField("14001");
	BoxLayout bl = new BoxLayout(this, BoxLayout.Y_AXIS);
	String inputIP;
	String inputPortServer;
	String inputPortBank;
	MyClient client;

	public ConnectionPanel(MainPanel mainPanel){
		this.mainPanel = mainPanel;
		this.setLayout(bl);
		this.add(IPaddress);
		this.add(serverPort);
		this.add(bankPort);
		this.add(connectionButton);
		connectionButton.addActionListener(this);
		IPaddress.setAlignmentX(Component.CENTER_ALIGNMENT);
		serverPort.setAlignmentX(Component.CENTER_ALIGNMENT);
		bankPort.setAlignmentX(Component.CENTER_ALIGNMENT);
		connectionButton.setAlignmentX(Component.CENTER_ALIGNMENT);		
	}
	
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == connectionButton){
			inputIP = IPaddress.getText();
			inputPortServer = serverPort.getText();
			inputPortBank = serverPort.getText();
			try {
				client = new MyClient("");
			} catch (RemoteException e1) {
				e1.printStackTrace();
			}
			if (client.connectTo(inputIP, inputPortServer, inputPortBank)){
				mainPanel.setRegister(client);
			} else {
				mainPanel.setConnection();
			}
		}
		
	}

}
