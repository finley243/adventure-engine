package com.github.finley243.adventureengine.condition;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Actor.Attribute;
import com.github.finley243.adventureengine.actor.ActorReference;
import com.github.finley243.adventureengine.world.item.Item;

public class ConditionEquippedItem extends Condition {

	private final ActorReference actor;
	// If tag is null, condition will be met if any item is equipped
	private final String tag;

	public ConditionEquippedItem(boolean invert, ActorReference actor, String tag) {
		super(invert);
		this.actor = actor;
		this.tag = tag;
	}

	@Override
	public boolean isMet(Actor subject) {
		Item equippedItem = actor.getActor(subject).getEquippedItem();
		if(equippedItem == null) return invert;
		return (tag == null || equippedItem.getTags().contains(tag)) != invert;
	}

	@Override
	public String getChoiceTag() {
		return null;
	}

}
