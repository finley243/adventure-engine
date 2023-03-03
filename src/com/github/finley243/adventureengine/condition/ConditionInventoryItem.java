package com.github.finley243.adventureengine.condition;

import com.github.finley243.adventureengine.ContextScript;
import com.github.finley243.adventureengine.variable.Variable;

public class ConditionInventoryItem extends Condition {

	private final Variable inventory;
	private final Variable itemID;
	private final boolean requireAll;

	public ConditionInventoryItem(boolean invert, Variable inventory, Variable itemID, boolean requireAll) {
		super(invert);
		if (inventory.getDataType() != Variable.DataType.INVENTORY)
			throw new IllegalArgumentException("Variable inventory must have type inventory");
		if (itemID.getDataType() != Variable.DataType.STRING && itemID.getDataType() != Variable.DataType.STRING_SET)
			throw new IllegalArgumentException("Variable itemID must have type string or string set");
		this.inventory = inventory;
		this.itemID = itemID;
		this.requireAll = requireAll;
	}

	@Override
	public boolean isMetInternal(ContextScript context) {
		if (itemID.getDataType() == Variable.DataType.STRING_SET) {
			for (String item : itemID.getValueStringSet(context)) {
				boolean hasItem = inventory.getValueInventory(context).hasItem(item);
				if (hasItem != requireAll) {
					return hasItem;
				}
			}
			return requireAll;
		} else {
			return inventory.getValueInventory(context).hasItem(itemID.getValueString(context));
		}
	}

}
