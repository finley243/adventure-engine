package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.expression.Expression;

import java.util.Map;

public class ScriptTransferItem extends Script {

    public enum TransferItemsType {
        INSTANCE, // Single item with instance ID
        COUNT, // Specified count of items with template ID
        TYPE, // All items with template ID
        ALL // All items in inventory
    }

    private final Expression inventoryOrigin;
    private final Expression inventoryTarget;
    private final Expression itemID;
    private final TransferItemsType transferType;
    private final int count;

    public ScriptTransferItem(Condition condition, Map<String, Expression> localParameters, Expression inventoryOrigin, Expression inventoryTarget, Expression itemID, TransferItemsType transferType, int count) {
        super(condition, localParameters);
        this.inventoryOrigin = inventoryOrigin;
        this.inventoryTarget = inventoryTarget;
        this.itemID = itemID;
        this.transferType = transferType;
        this.count = count;
    }

    @Override
    protected void executeSuccess(Context context) {
        switch (transferType) {
            case INSTANCE -> {
                String itemIDValue = itemID.getValueString(context);
                Item itemState = context.game().data().getItemState(itemIDValue);
                inventoryOrigin.getValueInventory(context).removeItem(itemState);
                inventoryTarget.getValueInventory(context).addItem(itemState);
            }
            case COUNT -> {
                String itemIDValue = itemID.getValueString(context);
                inventoryOrigin.getValueInventory(context).removeItems(itemIDValue, count);
                inventoryTarget.getValueInventory(context).addItems(itemIDValue, count);
            }
            case TYPE -> {
                String itemIDValue = itemID.getValueString(context);
                Inventory invOriginValue = inventoryOrigin.getValueInventory(context);
                int countInInventory = invOriginValue.itemCount(itemIDValue);
                invOriginValue.removeItems(itemIDValue, countInInventory);
                inventoryTarget.getValueInventory(context).addItems(itemIDValue, countInInventory);
            }
            case ALL -> {
                Inventory invOriginValue = inventoryOrigin.getValueInventory(context);
                Map<Item, Integer> allItems = invOriginValue.getItemMap();
                invOriginValue.clear();
                inventoryTarget.getValueInventory(context).addItems(allItems);
            }
        }
    }

}
