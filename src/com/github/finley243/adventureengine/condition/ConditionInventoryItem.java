package com.github.finley243.adventureengine.condition;

import com.github.finley243.adventureengine.ContextScript;
import com.github.finley243.adventureengine.actor.ActorReference;
import com.github.finley243.adventureengine.variable.Variable;

public class ConditionInventoryItem extends Condition {

	private final Variable inventory;
	private final Variable itemID;

	public ConditionInventoryItem(boolean invert, Variable inventory, Variable itemID) {
		super(invert);
		this.inventory = inventory;
		this.itemID = itemID;
	}

	@Override
	public boolean isMetInternal(ContextScript context) {
		return inventory.getValueInventory(context).hasItem(itemID.getValueString(context));
	}

}
