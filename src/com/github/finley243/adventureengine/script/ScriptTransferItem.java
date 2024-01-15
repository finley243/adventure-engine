package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Inventory;
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

    /*private final Expression inventoryOrigin;
    private final Expression inventoryTarget;
    private final Expression itemID;
    private final TransferItemsType transferType;
    private final Expression count;

    public ScriptTransferItem(Expression inventoryOrigin, Expression inventoryTarget, Expression itemID, TransferItemsType transferType, Expression count) {
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
    }*/

    @Override
    public ScriptReturnData execute(Context context) {
        Expression typeExpression = context.getLocalVariables().get("transferType").getExpression();
        if (typeExpression.getDataType() != Expression.DataType.STRING) return new ScriptReturnData(null, false, false, "Type parameter is not a string");
        TransferItemsType transferType;
        switch (typeExpression.getValueString()) {
            case "instance" -> transferType = TransferItemsType.INSTANCE;
            case "count" -> transferType = TransferItemsType.COUNT;
            case "type" -> transferType = TransferItemsType.TYPE;
            case "all" -> transferType = TransferItemsType.ALL;
            default -> {
                return new ScriptReturnData(null, false, false, "Type parameter is not a valid transfer type");
            }
        }
        Expression itemID = context.getLocalVariables().get("item").getExpression();
        Expression inventoryOrigin = context.getLocalVariables().get("from").getExpression();
        Expression inventoryTarget = context.getLocalVariables().get("to").getExpression();
        Expression count = context.getLocalVariables().get("count").getExpression();
        if (inventoryOrigin != null && inventoryOrigin.getDataType() != Expression.DataType.INVENTORY) return new ScriptReturnData(null, false, false, "From parameter is not an inventory");
        if (inventoryTarget != null && inventoryTarget.getDataType() != Expression.DataType.INVENTORY) return new ScriptReturnData(null, false, false, "To parameter is not an inventory");
        if (itemID != null && itemID.getDataType() != Expression.DataType.STRING) return new ScriptReturnData(null, false, false, "Item parameter is not a string");
        if (count != null && count.getDataType() != Expression.DataType.INTEGER) return new ScriptReturnData(null, false, false, "Count parameter is not an integer");
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
        return new ScriptReturnData(null, false, false, null);
    }

}
