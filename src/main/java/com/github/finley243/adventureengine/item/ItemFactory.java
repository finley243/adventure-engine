package com.github.finley243.adventureengine.item;

import com.github.finley243.adventureengine.gamedata.MutableRegistry;
import com.github.finley243.adventureengine.gamedata.Registry;
import com.github.finley243.adventureengine.item.template.*;

public class ItemFactory {

	// TODO - Possibly remove this dependency in the future (only allow passing an ItemTemplate instance to the create methods, not an ID)
	private final Registry<ItemTemplate> itemTemplateRegistry;
	private final MutableRegistry<Item> itemMutableRegistry;

	public ItemFactory(Registry<ItemTemplate> itemTemplateRegistry, MutableRegistry<Item> itemMutableRegistry) {
		this.itemTemplateRegistry = itemTemplateRegistry;
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

	public Item createWithGenID(String templateID) {
		ItemTemplate template = itemTemplateRegistry.getFromID(templateID);
		return createWithGenID(template);
	}

	public Item create(String templateID, String ID) {
		ItemTemplate template = itemTemplateRegistry.getFromID(templateID);
		return create(template, ID);
	}
	
}
