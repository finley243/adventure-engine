package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.ItemFactory;
import com.github.finley243.adventureengine.expression.Expression;

import java.util.Map;

public class ScriptAddItem extends Script {

	private final Expression inventory;
	private final Expression itemID;
	
	public ScriptAddItem(Condition condition, Map<String, Expression> localParameters, Expression inventory, Expression itemID) {
		super(condition, localParameters);
		this.inventory = inventory;
		this.itemID = itemID;
	}
	
	@Override
	public void executeSuccess(Context context) {
		Item item = ItemFactory.create(context.game(), context.game().data().getItem(itemID.getValueString(context)));
		inventory.getValueInventory(context).addItem(item);
	}
	
}
