package com.github.finley243.adventureengine.expression;

import com.github.finley243.adventureengine.item.Inventory;
import com.github.finley243.adventureengine.script.ScriptValueHolder;
import com.github.finley243.adventureengine.textgen.Noun;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public abstract class Expression {

    public enum DataType {
        BOOLEAN, INTEGER, FLOAT, STRING, SET, LIST, INVENTORY, NOUN, STAT_HOLDER
    }

    public abstract DataType getDataType();

    public boolean getValueBoolean() {
        throw new UnsupportedOperationException("Invalid data type function: boolean");
    }

    public int getValueInteger() {
        throw new UnsupportedOperationException("Invalid data type function: integer");
    }

    public float getValueFloat() {
        throw new UnsupportedOperationException("Invalid data type function: float");
    }

    public String getValueString() {
        throw new UnsupportedOperationException("Invalid data type function: string");
    }

    public Set<Expression> getValueSet() {
        throw new UnsupportedOperationException("Invalid data type function: set");
    }

    public List<Expression> getValueList() {
        throw new UnsupportedOperationException("Invalid data type function: list");
    }

    public Inventory getValueInventory() {
        throw new UnsupportedOperationException("Invalid data type function: inventory");
    }

    public Noun getValueNoun() {
        throw new UnsupportedOperationException("Invalid data type function: noun");
    }

    public ScriptValueHolder getValueStatHolder() {
        throw new UnsupportedOperationException("Invalid data type function: statHolder");
    }

    public boolean canCompareTo(Expression other) {
        if (this.getDataType() == DataType.SET || other.getDataType() == DataType.SET) {
            return false;
        } else if (this.getDataType() == DataType.LIST || other.getDataType() == DataType.LIST) {
            return false;
        } else if (this.getDataType() == DataType.INVENTORY || other.getDataType() == DataType.INVENTORY) {
            return false;
        } else if (this.getDataType() == DataType.NOUN || other.getDataType() == DataType.NOUN) {
            return false;
        } else if (this.getDataType() == DataType.STAT_HOLDER || other.getDataType() == DataType.STAT_HOLDER) {
            return false;
        }
        if (this.getDataType() == DataType.INTEGER || this.getDataType() == DataType.FLOAT) {
            return other.getDataType() == DataType.INTEGER || other.getDataType() == DataType.FLOAT;
        }
        return this.getDataType() == other.getDataType();
    }

    public static Expression bool(Boolean value) {
        if (value == null) return null;
        return new BooleanExpression(value);
    }

    public static Expression integer(Integer value) {
        if (value == null) return null;
        return new IntegerExpression(value);
    }

    public static Expression decimal(Float value) {
        if (value == null) return null;
        return new FloatExpression(value);
    }

    public static Expression string(String value) {
        if (value == null) return null;
        return new StringExpression(value);
    }

    public static <T> Expression set(Set<T> value, Function<T, Expression> expressionFunction) {
        if (value == null) return null;
        Set<Expression> convertedValue = new HashSet<>();
        for (T item : value) {
            convertedValue.add(expressionFunction.apply(item));
        }
        return new SetExpression(convertedValue);
    }

    public static <T> Expression list(List<T> value, Function<T, Expression> expressionFunction) {
        if (value == null) return null;
        List<Expression> convertedValue = new ArrayList<>();
        for (T item : value) {
            convertedValue.add(expressionFunction.apply(item));
        }
        return new ListExpression(convertedValue);
    }

    public static Expression inventory(Inventory value) {
        if (value == null) return null;
        return new InventoryExpression(value);
    }

    public static Expression noun(Noun value) {
        if (value == null) return null;
        return new NounExpression(value);
    }

    public static Expression valueHolder(ScriptValueHolder value) {
        if (value == null) return null;
        return new ValueHolderExpression(value);
    }

}
