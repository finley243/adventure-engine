package com.github.finley243.adventureengine.menu.data;

import com.github.finley243.adventureengine.world.item.Item;

public class MenuDataInventory extends MenuData {

	private final Item item;
	
	public MenuDataInventory(String prompt, boolean enabled, Item item) {
		super(prompt, enabled);
		this.item = item;
	}
	
	public Item getItem() {
		return item;
	}
	
}
