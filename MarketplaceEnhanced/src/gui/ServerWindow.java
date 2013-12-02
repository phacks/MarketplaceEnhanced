package gui;

import javax.swing.JFrame;

public class ServerWindow extends JFrame{
	// ServerWindow is displayed when a server is created, informs the user about the IP and the port

	public ServerWindow(int width, int height, String port, String title){
		this.setSize(width, height);
		this.setTitle(title);
		//this.setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ServerPanel serverPanel = new ServerPanel(this.getWidth(), this.getHeight(), port);
		this.setContentPane(serverPanel);
		this.setVisible(true);
	}

	
}