package gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import logic.Item;
import logic.MyClient;

@SuppressWarnings("serial")
public class AddItemsPanel extends JPanel implements ActionListener {

	private MainPanel mainPanel;
	private JButton addItemButton = new JButton("Add item");
	private JTextField nameItem = new JTextField("Name of the item");
	private JTextField descriptionItem = new JTextField("Description of the item");
	MyClient client;
	MyItemsPanel itemPanel;

	public AddItemsPanel(MainPanel mainPanel, MyClient client){
		this.client = client;
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.add(nameItem);
		this.add(descriptionItem);
		this.add(addItemButton);
		addItemButton.addActionListener(this);
		nameItem.setAlignmentX(Component.CENTER_ALIGNMENT);
		descriptionItem.setAlignmentX(Component.CENTER_ALIGNMENT);
		addItemButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		addItemButton.setSize(200, 100);
		nameItem.setMaximumSize(new Dimension(Integer.MAX_VALUE,nameItem.getPreferredSize().height));
		descriptionItem.setMaximumSize(new Dimension(Integer.MAX_VALUE,descriptionItem.getPreferredSize().height));

	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == addItemButton){
			String nameI = nameItem.getText();
			String descriptionI = descriptionItem.getText();
			Item item = new Item(nameI,client,descriptionI,"",false);
			client.addItem(item);
			JOptionPane.showMessageDialog(null,"Item " + nameI + " " + descriptionI + " added");
		}
	}
	
}
