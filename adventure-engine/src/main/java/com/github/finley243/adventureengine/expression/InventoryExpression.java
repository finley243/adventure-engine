package com.github.finley243.adventureengine.expression;

import com.github.finley243.adventureengine.item.Inventory;

public class InventoryExpression extends Expression {

    private final Inventory value;

    InventoryExpression(Inventory value) {
        this.value = value;
    }

    @Override
    public DataType getDataType() {
        return DataType.INVENTORY;
    }

    @Override
    public Inventory getValueInventory() {
        return value;
    }

    @Override
    public String toString() {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof InventoryExpression expression)) {
            return false;
        } else {
            return expression.value == this.value;
        }
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
