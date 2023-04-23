package com.github.finley243.adventureengine.expression;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.textgen.Noun;

import java.util.Set;

public abstract class Expression {

    public enum DataType {
        BOOLEAN, INTEGER, FLOAT, STRING, STRING_SET, INVENTORY, NOUN
    }

    public abstract DataType getDataType();

    public boolean getValueBoolean(Context context) {
        throw new UnsupportedOperationException("Invalid data type function: boolean");
    }

    public int getValueInteger(Context context) {
        throw new UnsupportedOperationException("Invalid data type function: integer");
    }

    public float getValueFloat(Context context) {
        throw new UnsupportedOperationException("Invalid data type function: float");
    }

    public String getValueString(Context context) {
        throw new UnsupportedOperationException("Invalid data type function: string");
    }

    public Set<String> getValueStringSet(Context context) {
        throw new UnsupportedOperationException("Invalid data type function: string set");
    }

    public Inventory getValueInventory(Context context) {
        throw new UnsupportedOperationException("Invalid data type function: inventory");
    }

    public Noun getValueNoun(Context context) {
        throw new UnsupportedOperationException("Invalid data type function: noun");
    }

    public boolean canCompareTo(Expression other) {
        if (this.getDataType() == DataType.STRING_SET || other.getDataType() == DataType.STRING_SET) {
            return false;
        } else if (this.getDataType() == DataType.INVENTORY || other.getDataType() == DataType.INVENTORY) {
            return false;
        } else if (this.getDataType() == DataType.NOUN || other.getDataType() == DataType.NOUN) {
            return false;
        }
        if (this.getDataType() == DataType.INTEGER || this.getDataType() == DataType.FLOAT) {
            return other.getDataType() == DataType.INTEGER || other.getDataType() == DataType.FLOAT;
        }
        return this.getDataType() == other.getDataType();
    }

    public static DataType dataTypeFromString(String name) {
        return switch (name) {
            case "boolean" -> DataType.BOOLEAN;
            case "int" -> DataType.INTEGER;
            case "float" -> DataType.FLOAT;
            case "string" -> DataType.STRING;
            case "stringSet" -> DataType.STRING_SET;
            case "inventory" -> DataType.INVENTORY;
            case "noun" -> DataType.NOUN;
            default -> null;
        };
    }

    public static ExpressionConstant convertToConstant(Expression expression, Context context) {
        return switch (expression.getDataType()) {
            case BOOLEAN -> new ExpressionConstant(expression.getValueBoolean(context));
            case INTEGER -> new ExpressionConstant(expression.getValueInteger(context));
            case FLOAT -> new ExpressionConstant(expression.getValueFloat(context));
            case STRING -> new ExpressionConstant(expression.getValueString(context));
            case STRING_SET -> new ExpressionConstant(expression.getValueStringSet(context));
            case INVENTORY -> new ExpressionConstant(expression.getValueInventory(context));
            case NOUN -> new ExpressionConstant(expression.getValueNoun(context));
        };
    }

}
