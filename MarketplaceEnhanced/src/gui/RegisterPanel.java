package gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import logic.ClientInterface;
import logic.MyClient;
import logic.RejectedException;

@SuppressWarnings({"serial", "unused"})
public class RegisterPanel extends JPanel implements ActionListener {

    private JButton signUpButton = new JButton("Sign up");
    private JButton signInButton = new JButton("Sign in");
    MainPanel mainPanel;
    String inputName;
    char[] inputPassword;
    String pwd;
    JTextField name = new JTextField("Name");
    JPasswordField password = new JPasswordField("Password");
    BoxLayout bl = new BoxLayout(this, BoxLayout.Y_AXIS);
    MyClient client;
    ClientInterface clientInterface;

    public RegisterPanel(MainPanel mainPanel, MyClient client) {
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
        inputName = name.getText();
        inputPassword = password.getPassword();
        pwd = new String(inputPassword);
        client.setName(inputName);
        if (e.getSource() == signUpButton) {

            try {
                client.getBank().registerClient(client);
                client.getServer().registerClient(client, pwd);
            } catch (RejectedException | RemoteException ex) {
                Logger.getLogger(RegisterPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (e.getSource() == signInButton) {
            try {
                if (client.getServer().checkClient(client, pwd) == true) {
                    mainPanel.setMarketplace(client, inputName);
                }

            } catch (RemoteException | SQLException ex) {
                Logger.getLogger(RegisterPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
