package com.github.finley243.adventureengine.expression;

import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.stat.StatHolder;
import com.github.finley243.adventureengine.textgen.Noun;

import java.util.Set;

public abstract class Expression {

    public enum DataType {
        BOOLEAN, INTEGER, FLOAT, STRING, STRING_SET, INVENTORY, NOUN, STAT_HOLDER
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

    public Set<String> getValueStringSet() {
        throw new UnsupportedOperationException("Invalid data type function: stringSet");
    }

    public Inventory getValueInventory() {
        throw new UnsupportedOperationException("Invalid data type function: inventory");
    }

    public Noun getValueNoun() {
        throw new UnsupportedOperationException("Invalid data type function: noun");
    }

    public StatHolder getValueStatHolder() {
        throw new UnsupportedOperationException("Invalid data type function: statHolder");
    }

    public boolean canCompareTo(Expression other) {
        if (this.getDataType() == DataType.STRING_SET || other.getDataType() == DataType.STRING_SET) {
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

    public static Expression convertToConstant(Expression expression) {
        if (expression == null) return null;
        return switch (expression.getDataType()) {
            case BOOLEAN -> new ExpressionConstantBoolean(expression.getValueBoolean());
            case INTEGER -> new ExpressionConstantInteger(expression.getValueInteger());
            case FLOAT -> new ExpressionConstantFloat(expression.getValueFloat());
            case STRING -> new ExpressionConstantString(expression.getValueString());
            case STRING_SET -> new ExpressionConstantStringSet(expression.getValueStringSet());
            case INVENTORY -> new ExpressionConstantInventory(expression.getValueInventory());
            case NOUN -> new ExpressionConstantNoun(expression.getValueNoun());
            case STAT_HOLDER -> new ExpressionConstantStatHolder(expression.getValueStatHolder());
        };
    }

    public static Expression constant(boolean value) {
        return new ExpressionConstantBoolean(value);
    }

    public static Expression constant(int value) {
        return new ExpressionConstantInteger(value);
    }

    public static Expression constant(float value) {
        return new ExpressionConstantFloat(value);
    }

    public static Expression constant(String value) {
        return new ExpressionConstantString(value);
    }

    public static Expression constant(Set<String> value) {
        return new ExpressionConstantStringSet(value);
    }

    public static Expression constant(Inventory value) {
        return new ExpressionConstantInventory(value);
    }

    public static Expression constant(Noun value) {
        return new ExpressionConstantNoun(value);
    }

    public static Expression constant(StatHolder value) {
        return new ExpressionConstantStatHolder(value);
    }

}
