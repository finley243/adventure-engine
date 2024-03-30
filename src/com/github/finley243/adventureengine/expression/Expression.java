package com.github.finley243.adventureengine.expression;

import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.stat.StatHolder;
import com.github.finley243.adventureengine.textgen.Noun;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    public StatHolder getValueStatHolder() {
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

    public static Expression constant(Object valueObject) {
        if (valueObject == null) return null;
        if (valueObject instanceof Expression expression) return expression;
        if (valueObject instanceof Set<?> set) {
            Set<Expression> expressionSet = new HashSet<>();
            for (Object elementObject : set) {
                Expression elementExpression = Expression.constant(elementObject);
                expressionSet.add(elementExpression);
            }
            return new ExpressionConstantSet(expressionSet);
        } else if (valueObject instanceof List<?> set) {
            List<Expression> expressionList = new ArrayList<>();
            for (Object elementObject : set) {
                Expression elementExpression = Expression.constant(elementObject);
                expressionList.add(elementExpression);
            }
            return new ExpressionConstantList(expressionList);
        }
        switch (valueObject) {
            case Boolean value -> {
                return new ExpressionConstantBoolean(value);
            }
            case Float value -> {
                return new ExpressionConstantFloat(value);
            }
            case Integer value -> {
                return new ExpressionConstantInteger(value);
            }
            case String value -> {
                return new ExpressionConstantString(value);
            }
            case Inventory value -> {
                return new ExpressionConstantInventory(value);
            }
            case StatHolder value -> {
                return new ExpressionConstantStatHolder(value);
            }
            case Noun value -> {
                return new ExpressionConstantNoun(value);
            }
            default -> throw new IllegalArgumentException("Expression is not a valid type");
        }
    }

}
