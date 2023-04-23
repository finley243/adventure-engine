package com.github.finley243.adventureengine.condition;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;

public class ConditionInventoryItem extends Condition {

	private final Expression inventory;
	private final Expression itemID;
	private final boolean requireAll;

	public ConditionInventoryItem(boolean invert, Expression inventory, Expression itemID, boolean requireAll) {
		super(invert);
		if (inventory.getDataType() != Expression.DataType.INVENTORY)
			throw new IllegalArgumentException("Variable inventory must have type inventory");
		if (itemID.getDataType() != Expression.DataType.STRING && itemID.getDataType() != Expression.DataType.STRING_SET)
			throw new IllegalArgumentException("Variable itemID must have type string or string set");
		this.inventory = inventory;
		this.itemID = itemID;
		this.requireAll = requireAll;
	}

	@Override
	public boolean isMetInternal(Context context) {
		if (itemID.getDataType() == Expression.DataType.STRING_SET) {
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
