package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Data;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ActorReference;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.world.item.Item;
import com.github.finley243.adventureengine.world.template.ItemFactory;

public class ScriptAddItem extends Script {

	private final ActorReference actor;
	private final String itemID;
	
	public ScriptAddItem(Condition condition, ActorReference actor, String itemID) {
		super(condition);
		this.actor = actor;
		this.itemID = itemID;
	}
	
	@Override
	public void executeSuccess(Actor subject) {
		Item item = ItemFactory.create(subject.game(), subject.game().data().getItem(itemID));
		actor.getActor(subject).inventory().addItem(item);
	}
	
}
