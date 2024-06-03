package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.item.Item;

import java.util.Map;

public class ScriptTransferItem extends Script {

    public ScriptTransferItem(int line) {
        super(line);
    }

    public enum TransferItemsType {
        INSTANCE, // Single item with instance ID
        COUNT, // Specified count of items with template ID
        TYPE, // All items with template ID
        ALL // All items in inventory
    }

    @Override
    public ScriptReturnData execute(Context context) {
        Expression typeExpression = context.getLocalVariables().get("transferType").getExpression();
        if (typeExpression.getDataType() != Expression.DataType.STRING) return new ScriptReturnData(null, null, new ScriptErrorData("Type parameter is not a string", getLine()));
        TransferItemsType transferType;
        switch (typeExpression.getValueString()) {
            case "instance" -> transferType = TransferItemsType.INSTANCE;
            case "count" -> transferType = TransferItemsType.COUNT;
            case "type" -> transferType = TransferItemsType.TYPE;
            case "all" -> transferType = TransferItemsType.ALL;
            default -> {
                return new ScriptReturnData(null, null, new ScriptErrorData("Type parameter is not a valid transfer type", getLine()));
            }
        }
        Expression itemID = context.getLocalVariables().get("item").getExpression();
        Expression inventoryOrigin = context.getLocalVariables().get("from").getExpression();
        Expression inventoryTarget = context.getLocalVariables().get("to").getExpression();
        Expression count = context.getLocalVariables().get("count").getExpression();
        if (inventoryOrigin != null && inventoryOrigin.getDataType() != Expression.DataType.INVENTORY) return new ScriptReturnData(null, null, new ScriptErrorData("From parameter is not an inventory", getLine()));
        if (inventoryTarget != null && inventoryTarget.getDataType() != Expression.DataType.INVENTORY) return new ScriptReturnData(null, null, new ScriptErrorData("To parameter is not an inventory", getLine()));
        if (itemID != null && itemID.getDataType() != Expression.DataType.STRING) return new ScriptReturnData(null, null, new ScriptErrorData("Item parameter is not a string", getLine()));
        if (count != null && count.getDataType() != Expression.DataType.INTEGER) return new ScriptReturnData(null, null, new ScriptErrorData("Count parameter is not an integer", getLine()));
        switch (transferType) {
            case INSTANCE -> {
                String itemIDValue = itemID.getValueString();
                Item itemState = context.game().data().getItemInstance(itemIDValue);
                inventoryOrigin.getValueInventory().removeItem(itemState);
                if (inventoryTarget != null) {
                    inventoryTarget.getValueInventory().addItem(itemState);
                }
            }
            case COUNT -> {
                String itemIDValue = itemID.getValueString();
                int countValue = count.getValueInteger();
                if (inventoryOrigin != null) {
                    inventoryOrigin.getValueInventory().removeItems(itemIDValue, countValue);
                }
                if (inventoryTarget != null) {
                    inventoryTarget.getValueInventory().addItems(itemIDValue, countValue);
                }
            }
            case TYPE -> {
                String itemIDValue = itemID.getValueString();
                Inventory invOriginValue = inventoryOrigin.getValueInventory();
                int countInInventory = invOriginValue.itemCount(itemIDValue);
                invOriginValue.removeItems(itemIDValue, countInInventory);
                if (inventoryTarget != null) {
                    inventoryTarget.getValueInventory().addItems(itemIDValue, countInInventory);
                }
            }
            case ALL -> {
                Inventory invOriginValue = inventoryOrigin.getValueInventory();
                Map<Item, Integer> allItems = invOriginValue.getItemMap();
                invOriginValue.clear();
                if (inventoryTarget != null) {
                    inventoryTarget.getValueInventory().addItems(allItems);
                }
            }
        }
        return new ScriptReturnData(null, null, null);
    }

}
