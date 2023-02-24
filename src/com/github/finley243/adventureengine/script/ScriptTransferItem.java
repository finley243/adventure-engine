package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.ContextScript;
import com.github.finley243.adventureengine.actor.ActorReference;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.ItemFactory;
import com.github.finley243.adventureengine.variable.Variable;

import java.util.Map;

public class ScriptTransferItem extends Script {

    private final Variable inventoryOrigin;
    private final Variable inventoryTarget;
    private final String item;
    private final boolean all;
    private final int count;

    public ScriptTransferItem(Condition condition, Variable inventoryOrigin, Variable inventoryTarget, String item, boolean all, int count) {
        super(condition);
        this.inventoryOrigin = inventoryOrigin;
        this.inventoryTarget = inventoryTarget;
        this.item = item;
        this.all = all;
        this.count = count;
    }

    @Override
    protected void executeSuccess(ContextScript context) {
        Item itemPlaceholder = ItemFactory.create(context.game(), item);
        if (all) {
            if (item == null) { // All items in inventory
                Map<Item, Integer> allItems = inventoryOrigin.getValueInventory(context).getItemMap();
                inventoryOrigin.getValueInventory(context).clear();
                inventoryTarget.getValueInventory(context).addItems(allItems);
            } else { // All items of type
                int countInInventory = inventoryOrigin.getValueInventory(context).itemCount(itemPlaceholder);
                inventoryOrigin.getValueInventory(context).removeItems(itemPlaceholder, countInInventory);
                inventoryTarget.getValueInventory(context).addItems(itemPlaceholder, countInInventory);
            }
        } else {
            inventoryOrigin.getValueInventory(context).removeItems(itemPlaceholder, count);
            inventoryTarget.getValueInventory(context).addItems(itemPlaceholder, count);
        }
    }

}
