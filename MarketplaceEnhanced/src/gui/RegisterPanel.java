package gui;


import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.rmi.Remote;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import logic.ClientInterface;
import logic.MyClient;


@SuppressWarnings({ "serial", "unused" })
public class RegisterPanel extends JPanel implements ActionListener{

	private JButton registerButton = new JButton("Register");
	MainPanel mainPanel;
	String inputName;
	JTextField name = new JTextField("Name");
	BoxLayout bl = new BoxLayout(this, BoxLayout.Y_AXIS);
	MyClient client;
	ClientInterface clientInterface;

	public RegisterPanel(MainPanel mainPanel, MyClient client){
		this.mainPanel = mainPanel;
		this.client = client;
		this.setLayout(bl);
		this.add(name);
		this.add(registerButton);
		registerButton.addActionListener(this);
		name.setAlignmentX(Component.CENTER_ALIGNMENT);
		registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);		
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == registerButton){
			inputName = name.getText();
			client.setName(inputName);
			try {
				client.getBank().registerClient(client);
				client.getServer().registerClient(client);
				List<ClientInterface> clientTable = client.getServer().getClients();
				mainPanel.setMarketplace(client,inputName);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

}
