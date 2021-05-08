package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Data;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.world.item.Item;
import com.github.finley243.adventureengine.world.template.ItemFactory;

public class ScriptAddItem implements Script {

	private String itemID;
	
	public ScriptAddItem(String itemID) {
		this.itemID = itemID;
	}
	
	@Override
	public void execute(Actor target) {
		Item item = ItemFactory.create(Data.getItem(itemID));
		target.inventory().addItem(item);
	}
	
}
