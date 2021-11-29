package com.github.finley243.adventureengine.menu.data;

import com.github.finley243.adventureengine.world.item.Item;

public class MenuDataEquipped extends MenuData {

	private final Item item;
	
	public MenuDataEquipped(String prompt, String fullPrompt, boolean enabled, Item item) {
		super(prompt, fullPrompt, enabled);
		this.item = item;
	}
	
	public Item getItem() {
		return item;
	}

}
