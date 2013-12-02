package logic;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Item implements Serializable {
	private String name;
	private ClientInterface owner;
	private String description;
	private String price;
	private int id;
	private boolean onSale = false;
	static int counter = 1;

	public Item(String name, ClientInterface owner, String description,String price,boolean onSale){
		this.setName(name);
		this.setOwner(owner);
		this.setDescription(description);
		this.setPrice(price);
		this.setonSale(onSale);
		setId(counter);
		counter ++;
	}

	public String getPrice(){
		return price;
	}
	
	public void setPrice(String price) {
		this.price = price;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ClientInterface getOwner() {
		return owner;
	}
	
	public void setOwner(ClientInterface owner) {
		this.owner = owner;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean getonSale() {
		return onSale;
	}

	public void setonSale(boolean onSale) {
		this.onSale = onSale;	
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
}
