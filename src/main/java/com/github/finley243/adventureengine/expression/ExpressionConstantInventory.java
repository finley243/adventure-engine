package com.github.finley243.adventureengine.expression;

import com.github.finley243.adventureengine.actor.Inventory;

public class ExpressionConstantInventory extends Expression {

    private final Inventory value;

    public ExpressionConstantInventory(Inventory value) {
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
        if (!(o instanceof ExpressionConstantInventory expression)) {
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
