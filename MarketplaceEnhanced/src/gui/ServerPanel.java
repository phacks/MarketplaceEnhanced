package gui;

import java.awt.Color;
import java.awt.Component;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ServerPanel extends JPanel {
	
	String IPaddress;
	
	BoxLayout layoutServerPanel = new BoxLayout(this, BoxLayout.Y_AXIS);
	
	
	public ServerPanel(int width, int height, String port){
		try {
			IPaddress = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JLabel address = new JLabel("IP Address : " + IPaddress);
		JLabel portLabel = new JLabel("Port : " + port);
		this.setSize(width, height);
		this.setBackground(Color.white);
		this.setLayout(layoutServerPanel);
		this.add(address);
		this.add(portLabel);
		address.setAlignmentX(Component.CENTER_ALIGNMENT);
		portLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
	}
}