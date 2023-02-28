package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.ContextScript;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.ItemFactory;
import com.github.finley243.adventureengine.variable.Variable;

public class ScriptAddItem extends Script {

	private final Variable inventory;
	private final Variable itemID;
	
	public ScriptAddItem(Condition condition, Variable inventory, Variable itemID) {
		super(condition);
		this.inventory = inventory;
		this.itemID = itemID;
	}
	
	@Override
	public void executeSuccess(ContextScript context) {
		Item item = ItemFactory.create(context.game(), context.game().data().getItem(itemID.getValueString(context)));
		inventory.getValueInventory(context).addItem(item);
	}
	
}
