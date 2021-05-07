package com.github.finley243.adventureengine.world.template;

public class StatsItem {

	private String ID;
	private String name;
	private int price;
	
	public StatsItem(String ID, String name, int price) {
		this.ID = ID;
		this.name = name;
		this.price = price;
	}
	
	public String getID() {
		return ID;
	}
	
	public String getName() {
		return name;
	}
	
	public int getPrice() {
		return price;
	}
	
}
