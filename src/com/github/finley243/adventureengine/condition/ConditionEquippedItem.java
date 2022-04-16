package com.github.finley243.adventureengine.condition;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ActorReference;
import com.github.finley243.adventureengine.world.item.Item;

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
	public boolean isMet(Actor subject) {
		Item equippedItem = actor.getActor(subject).equipmentComponent().getEquippedItem();
		if(equippedItem == null) return invert;
		if(itemID != null) {
			return equippedItem.getTemplate().getID().equals(itemID) != invert;
		}
		return (tag == null || equippedItem.getTemplate().getTags().contains(tag)) != invert;
	}

	@Override
	public String getChoiceTag() {
		return null;
	}

}
