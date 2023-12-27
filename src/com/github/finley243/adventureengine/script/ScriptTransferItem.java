package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.item.Item;

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
    private final Expression count;

    public ScriptTransferItem(Condition condition, Expression inventoryOrigin, Expression inventoryTarget, Expression itemID, TransferItemsType transferType, Expression count) {
        super(condition);
        switch (transferType) {
            case INSTANCE, TYPE, ALL -> {
                if (inventoryOrigin == null) throw new IllegalArgumentException("ScriptTransferItem of type " + transferType + " must specify an origin inventory");
            }
            case COUNT -> {
                if (inventoryOrigin == null && inventoryTarget == null) throw new IllegalArgumentException("ScriptTransferItem of type COUNT must specify either an origin or target inventory");
            }
        }
        this.inventoryOrigin = inventoryOrigin;
        this.inventoryTarget = inventoryTarget;
        this.itemID = itemID;
        this.transferType = transferType;
        this.count = count;
    }

    @Override
    protected void executeSuccess(Context context, ScriptReturnTarget returnTarget) {
        if (inventoryOrigin != null && inventoryOrigin.getDataType(context) != Expression.DataType.INVENTORY) throw new IllegalArgumentException("ScriptTransferItem inventoryOrigin is not an inventory");
        if (inventoryTarget != null && inventoryTarget.getDataType(context) != Expression.DataType.INVENTORY) throw new IllegalArgumentException("ScriptTransferItem inventoryTarget is not an inventory");
        if (itemID != null && itemID.getDataType(context) != Expression.DataType.STRING) throw new IllegalArgumentException("ScriptTransferItem itemID is not a string");
        if (count != null && count.getDataType(context) != Expression.DataType.INTEGER) throw new IllegalArgumentException("ScriptTransferItem count is not an integer");
        switch (transferType) {
            case INSTANCE -> {
                String itemIDValue = itemID.getValueString(context);
                Item itemState = context.game().data().getItemInstance(itemIDValue);
                inventoryOrigin.getValueInventory(context).removeItem(itemState);
                if (inventoryTarget != null) {
                    inventoryTarget.getValueInventory(context).addItem(itemState);
                }
            }
            case COUNT -> {
                String itemIDValue = itemID.getValueString(context);
                int countValue = count.getValueInteger(context);
                if (inventoryOrigin != null) {
                    inventoryOrigin.getValueInventory(context).removeItems(itemIDValue, countValue);
                }
                if (inventoryTarget != null) {
                    inventoryTarget.getValueInventory(context).addItems(itemIDValue, countValue);
                }
            }
            case TYPE -> {
                String itemIDValue = itemID.getValueString(context);
                Inventory invOriginValue = inventoryOrigin.getValueInventory(context);
                int countInInventory = invOriginValue.itemCount(itemIDValue);
                invOriginValue.removeItems(itemIDValue, countInInventory);
                if (inventoryTarget != null) {
                    inventoryTarget.getValueInventory(context).addItems(itemIDValue, countInInventory);
                }
            }
            case ALL -> {
                Inventory invOriginValue = inventoryOrigin.getValueInventory(context);
                Map<Item, Integer> allItems = invOriginValue.getItemMap();
                invOriginValue.clear();
                if (inventoryTarget != null) {
                    inventoryTarget.getValueInventory(context).addItems(allItems);
                }
            }
        }
        sendReturn(new ScriptReturn(null, false, false, null));
    }

}
