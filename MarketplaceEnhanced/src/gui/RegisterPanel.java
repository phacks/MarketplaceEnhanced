package gui;


import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import logic.ClientInterface;
import logic.MyClient;
import logic.RejectedException;


@SuppressWarnings({ "serial", "unused" })
public class RegisterPanel extends JPanel implements ActionListener{

	private JButton signUpButton = new JButton("Sign up");
        private JButton signInButton = new JButton("Sign in");
	MainPanel mainPanel;
	String inputName;
        char[] inputPassword;
	JTextField name = new JTextField("Name");
        JPasswordField password = new JPasswordField("Password");
	BoxLayout bl = new BoxLayout(this, BoxLayout.Y_AXIS);
	MyClient client;
	ClientInterface clientInterface;

	public RegisterPanel(MainPanel mainPanel, MyClient client){
		this.mainPanel = mainPanel;
		this.client = client;
		this.setLayout(bl);
		this.add(name);
                this.add(password);
		this.add(signUpButton);
                this.add(signInButton);
		signUpButton.addActionListener(this);
                signInButton.addActionListener(this);
		name.setAlignmentX(Component.CENTER_ALIGNMENT);
		signUpButton.setAlignmentX(Component.CENTER_ALIGNMENT);	
                signInButton.setAlignmentX(Component.CENTER_ALIGNMENT);	
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == signUpButton){
			inputName = name.getText();
                        inputPassword = password.getPassword();
			client.setName(inputName);
                    try {
                        client.getServer().registerClient(client, inputPassword);
                    } catch (RejectedException ex) {
                         Logger.getLogger(RegisterPanel.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (RemoteException ex) {
                        Logger.getLogger(RegisterPanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    // List<ClientInterface> clientTable = client.getServer().getClients();
                        // mainPanel.setMarketplace(client,inputName);
                       
		}
	}

}
