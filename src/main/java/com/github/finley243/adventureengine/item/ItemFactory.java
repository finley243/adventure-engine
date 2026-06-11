package com.github.finley243.adventureengine.item;

import com.github.finley243.adventureengine.gamedata.MutableRegistry;
import com.github.finley243.adventureengine.item.template.*;

public class ItemFactory {

	private final MutableRegistry<Item> itemMutableRegistry;

	public ItemFactory(MutableRegistry<Item> itemMutableRegistry) {
		this.itemMutableRegistry = itemMutableRegistry;
	}
	
	public Item createWithGenID(ItemTemplate template) {
		String ID = template.generateInstanceID();
		return create(template, ID);
	}

	public Item create(ItemTemplate template, String ID) {
		if (ID == null) throw new IllegalArgumentException("Item ID cannot be null");
		if (template == null) throw new IllegalArgumentException("Item template cannot be null");
		Item item = new Item(ID, template);
		itemMutableRegistry.add(ID, item);
		//item.onInit(game);
		return item;
	}
	
}
