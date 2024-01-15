package com.github.finley243.adventureengine.expression;

import com.github.finley243.adventureengine.actor.Inventory;

public class ExpressionInventoryContains extends Expression {

    private final Expression inventory;
    private final Expression itemID;
    private final boolean requireAll;

    public ExpressionInventoryContains(Expression inventory, Expression itemID, boolean requireAll) {
        this.inventory = inventory;
        this.itemID = itemID;
        this.requireAll = requireAll;
    }

    @Override
    public DataType getDataType() {
        return DataType.BOOLEAN;
    }

    @Override
    public boolean getValueBoolean() {
        if (inventory.getDataType() != Expression.DataType.INVENTORY)
            throw new IllegalArgumentException("Expression inventory is not an inventory");
        if (itemID.getDataType() != Expression.DataType.STRING && itemID.getDataType() != Expression.DataType.STRING_SET)
            throw new IllegalArgumentException("Expression itemID is not a string or string set");
        if (itemID.getDataType() == Expression.DataType.STRING_SET) {
            Inventory inventoryValue = inventory.getValueInventory();
            for (String item : itemID.getValueStringSet()) {
                boolean hasItem = inventoryValue.hasItem(item);
                if (hasItem != requireAll) {
                    return hasItem;
                }
            }
            return requireAll;
        } else {
            return inventory.getValueInventory().hasItem(itemID.getValueString());
        }
    }

}
