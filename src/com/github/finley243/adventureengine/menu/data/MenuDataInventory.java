package com.github.finley243.adventureengine.menu.data;

import com.github.finley243.adventureengine.world.item.Item;

public class MenuDataInventory extends MenuData {

	private Item item;
	
	public MenuDataInventory(String prompt, Item item) {
		super(prompt);
		this.item = item;
	}
	
	public Item getItem() {
		return item;
	}
	
}
