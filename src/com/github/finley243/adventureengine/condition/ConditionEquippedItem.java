package com.github.finley243.adventureengine.condition;

import com.github.finley243.adventureengine.ContextScript;
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
	public boolean isMetInternal(ContextScript context) {
		Item equippedItem = actor.getActor(context).getEquipmentComponent().getEquippedItem();
		if(equippedItem == null) return false;
		if(itemID != null) {
			return equippedItem.getTemplate().getID().equals(itemID);
		}
		return (tag == null || equippedItem.getTemplate().getTags().contains(tag));
	}

}
