package gui;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public class Window extends JFrame {

	MainPanel mainPanel;

	public Window (int width, int height) {
		this.setSize(width, height);
		this.setTitle("Client");
		this.setLocationRelativeTo(null);
		this.mainPanel = new MainPanel(this.getWidth(), this.getHeight());
		this.setContentPane(mainPanel);
		this.setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}	

}