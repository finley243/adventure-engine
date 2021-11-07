package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Data;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ActorReference;
import com.github.finley243.adventureengine.world.item.Item;
import com.github.finley243.adventureengine.world.template.ItemFactory;

public class ScriptAddItem implements Script {

	private final ActorReference actor;
	private final String itemID;
	
	public ScriptAddItem(ActorReference actor, String itemID) {
		this.actor = actor;
		this.itemID = itemID;
	}
	
	@Override
	public void execute(Actor subject) {
		Item item = ItemFactory.create(Data.getItem(itemID));
		actor.getActor(subject).inventory().addItem(item);
	}
	
}
