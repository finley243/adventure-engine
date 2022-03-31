package com.github.finley243.adventureengine.condition;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ActorReference;
import com.github.finley243.adventureengine.world.item.Item;

public class ConditionInventoryItem extends Condition {

	private final ActorReference actor;
	private final String tag;
	private final String itemID;

	public ConditionInventoryItem(boolean invert, ActorReference actor, String tag, String itemID) {
		super(invert);
		this.actor = actor;
		this.tag = tag;
		this.itemID = itemID;
	}

	@Override
	public boolean isMet(Actor subject) {
		if(itemID != null) {
			return actor.getActor(subject).inventory().hasItemWithID(itemID) != invert;
		} else {
			return actor.getActor(subject).inventory().hasItemWithTag(tag) != invert;
		}
	}

	@Override
	public String getChoiceTag() {
		return null;
	}

}
