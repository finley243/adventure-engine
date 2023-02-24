package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.ContextScript;
import com.github.finley243.adventureengine.actor.ActorReference;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.ItemFactory;

public class ScriptAddItem extends Script {

	private final ActorReference actor;
	private final String itemID;
	
	public ScriptAddItem(Condition condition, ActorReference actor, String itemID) {
		super(condition);
		this.actor = actor;
		this.itemID = itemID;
	}
	
	@Override
	public void executeSuccess(ContextScript context) {
		Item item = ItemFactory.create(context.game(), context.game().data().getItem(itemID));
		actor.getActor(context).getInventory().addItem(item);
	}
	
}
