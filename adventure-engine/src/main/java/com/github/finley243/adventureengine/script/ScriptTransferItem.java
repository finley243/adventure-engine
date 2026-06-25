package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.item.Inventory;
import com.github.finley243.adventureengine.item.Item;

import java.util.Map;

public class ScriptTransferItem extends Script {

    public ScriptTransferItem(ScriptTraceData traceData) {
        super(traceData);
    }

    public enum TransferItemsType {
        INSTANCE, // Single item with instance ID
        COUNT, // Specified count of items with template ID
        TYPE, // All items with template ID
        ALL // All items in inventory
    }

    @Override
    ScriptReturnData execute(ScriptRuntime scriptRuntime, Context context) {
        Expression typeExpression = context.getLocalVariables().get("transferType").getExpression();
        if (typeExpression.getDataType() != Expression.DataType.STRING) return new ScriptReturnData(null, null, new ScriptErrorData("Type parameter is not a string", getTraceData()));
        TransferItemsType transferType;
        switch (typeExpression.getValueString()) {
            case "instance" -> transferType = TransferItemsType.INSTANCE;
            case "count" -> transferType = TransferItemsType.COUNT;
            case "type" -> transferType = TransferItemsType.TYPE;
            case "all" -> transferType = TransferItemsType.ALL;
            default -> {
                return new ScriptReturnData(null, null, new ScriptErrorData("Type parameter is not a valid transfer type", getTraceData()));
            }
        }
        Expression itemID = context.getLocalVariables().get("item").getExpression();
        Expression inventoryOriginExpression = context.getLocalVariables().get("from").getExpression();
        Expression inventoryTargetExpression = context.getLocalVariables().get("to").getExpression();
        Expression count = context.getLocalVariables().get("count").getExpression();
        if (inventoryOriginExpression != null && inventoryOriginExpression.getDataType() != Expression.DataType.INVENTORY) return new ScriptReturnData(null, null, new ScriptErrorData("From parameter is not an inventory", getTraceData()));
        if (inventoryTargetExpression != null && inventoryTargetExpression.getDataType() != Expression.DataType.INVENTORY) return new ScriptReturnData(null, null, new ScriptErrorData("To parameter is not an inventory", getTraceData()));
        Inventory inventoryOrigin = inventoryOriginExpression == null ? null : inventoryOriginExpression.getValueInventory();
        Inventory inventoryTarget = inventoryTargetExpression == null ? null : inventoryTargetExpression.getValueInventory();
        if (itemID != null && itemID.getDataType() != Expression.DataType.STRING) return new ScriptReturnData(null, null, new ScriptErrorData("Item parameter is not a string", getTraceData()));
        if (count != null && count.getDataType() != Expression.DataType.INTEGER) return new ScriptReturnData(null, null, new ScriptErrorData("Count parameter is not an integer", getTraceData()));
        switch (transferType) {
            case INSTANCE -> {
                String itemIDValue = itemID.getValueString();
                Item itemState = scriptRuntime.getItem(itemIDValue);
                if (inventoryOrigin == null) return new ScriptReturnData(null, null, new ScriptErrorData("From parameter cannot be null for instance transfer", getTraceData()));
                inventoryOrigin.removeItem(itemState);
                if (inventoryTarget != null) {
                    inventoryTarget.addItem(itemState);
                }
            }
            case COUNT -> {
                String itemIDValue = itemID.getValueString();
                int countValue = count.getValueInteger();
                if (inventoryOrigin != null) {
                    inventoryOrigin.removeItems(itemIDValue, countValue);
                }
                if (inventoryTarget != null) {
                    inventoryTarget.addItems(itemIDValue, countValue);
                }
            }
            case TYPE -> {
                String itemIDValue = itemID.getValueString();
                if (inventoryOrigin == null) return new ScriptReturnData(null, null, new ScriptErrorData("From parameter cannot be null for type transfer", getTraceData()));
                int countInInventory = inventoryOrigin.itemCount(itemIDValue);
                inventoryOrigin.removeItems(itemIDValue, countInInventory);
                if (inventoryTarget != null) {
                    inventoryTarget.addItems(itemIDValue, countInInventory);
                }
            }
            case ALL -> {
                if (inventoryOrigin == null) return new ScriptReturnData(null, null, new ScriptErrorData("From parameter cannot be null for all transfer", getTraceData()));
                Map<Item, Integer> allItems = inventoryOrigin.getItemMap();
                if (inventoryTarget != null) {
                    inventoryTarget.addItems(allItems);
                }
            }
        }
        return new ScriptReturnData(null, null, null);
    }

}
