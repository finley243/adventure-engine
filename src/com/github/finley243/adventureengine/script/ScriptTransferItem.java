package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.ContextScript;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.ItemFactory;
import com.github.finley243.adventureengine.variable.Variable;

import java.util.Map;

public class ScriptTransferItem extends Script {

    public enum TransferItemsType {
        INSTANCE, // Single item with instance ID
        COUNT, // Specified count of items with template ID
        TYPE, // All items with template ID
        ALL // All items in inventory
    }

    private final Variable inventoryOrigin;
    private final Variable inventoryTarget;
    private final Variable itemID;
    private final TransferItemsType transferType;
    private final int count;

    public ScriptTransferItem(Condition condition, Variable inventoryOrigin, Variable inventoryTarget, Variable itemID, TransferItemsType transferType, int count) {
        super(condition);
        this.inventoryOrigin = inventoryOrigin;
        this.inventoryTarget = inventoryTarget;
        this.itemID = itemID;
        this.transferType = transferType;
        this.count = count;
    }

    @Override
    protected void executeSuccess(ContextScript context) {
        switch (transferType) {
            case INSTANCE -> {
                inventoryOrigin.getValueInventory(context).removeItem(context.game().data().getItemState(itemID.getValueString(context)));
                inventoryTarget.getValueInventory(context).addItem(context.game().data().getItemState(itemID.getValueString(context)));
            }
            case COUNT -> {
                inventoryOrigin.getValueInventory(context).removeItems(itemID.getValueString(context), count);
                inventoryTarget.getValueInventory(context).addItems(itemID.getValueString(context), count);
            }
            case TYPE -> {
                int countInInventory = inventoryOrigin.getValueInventory(context).itemCount(itemID.getValueString(context));
                inventoryOrigin.getValueInventory(context).removeItems(itemID.getValueString(context), countInInventory);
                inventoryTarget.getValueInventory(context).addItems(itemID.getValueString(context), countInInventory);
            }
            case ALL -> {
                Map<Item, Integer> allItems = inventoryOrigin.getValueInventory(context).getItemMap();
                inventoryOrigin.getValueInventory(context).clear();
                inventoryTarget.getValueInventory(context).addItems(allItems);
            }
        }
    }

}
