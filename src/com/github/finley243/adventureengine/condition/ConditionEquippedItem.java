package com.github.finley243.adventureengine.condition;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ActorReference;
import com.github.finley243.adventureengine.item.Item;

public class ConditionEquippedItem extends Condition {

	private final ActorReference actor;
	// If tag and itemID are null, condition will be met if any item is equipped
	private final String tag;
	private final String itemID;

	public ConditionEquippedItem(boolean invert, ActorReference actor, String tag, String itemID) {
		super(invert);
		this.actor = actor;
		this.tag = tag;
		this.itemID = itemID;
	}

	@Override
	public boolean isMetInternal(Actor subject) {
		Item equippedItem = actor.getActor(subject).equipmentComponent().getEquippedItem();
		if(equippedItem == null) return false;
		if(itemID != null) {
			return equippedItem.getTemplate().getID().equals(itemID);
		}
		return (tag == null || equippedItem.getTemplate().getTags().contains(tag));
	}

}
