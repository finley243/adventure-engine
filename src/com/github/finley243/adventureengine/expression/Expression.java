package com.github.finley243.adventureengine.expression;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.textgen.Noun;

import java.util.Set;

public abstract class Expression {

    public enum DataType {
        BOOLEAN, INTEGER, FLOAT, STRING, STRING_SET, INVENTORY, NOUN
    }

    public abstract DataType getDataType(Context context);

    // TODO - Remove context from expression function parameters (if a context is needed, it should probably be a script, not an expression)
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

    public boolean canCompareTo(Expression other, Context context) {
        if (this.getDataType(context) == DataType.STRING_SET || other.getDataType(context) == DataType.STRING_SET) {
            return false;
        } else if (this.getDataType(context) == DataType.INVENTORY || other.getDataType(context) == DataType.INVENTORY) {
            return false;
        } else if (this.getDataType(context) == DataType.NOUN || other.getDataType(context) == DataType.NOUN) {
            return false;
        }
        if (this.getDataType(context) == DataType.INTEGER || this.getDataType(context) == DataType.FLOAT) {
            return other.getDataType(context) == DataType.INTEGER || other.getDataType(context) == DataType.FLOAT;
        }
        return this.getDataType(context) == other.getDataType(context);
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

    public static Expression convertToConstant(Expression expression, Context context) {
        if (expression == null) return null;
        return switch (expression.getDataType(context)) {
            case BOOLEAN -> new ExpressionConstantBoolean(expression.getValueBoolean(context));
            case INTEGER -> new ExpressionConstantInteger(expression.getValueInteger(context));
            case FLOAT -> new ExpressionConstantFloat(expression.getValueFloat(context));
            case STRING -> new ExpressionConstantString(expression.getValueString(context));
            case STRING_SET -> new ExpressionConstantStringSet(expression.getValueStringSet(context));
            case INVENTORY -> new ExpressionConstantInventory(expression.getValueInventory(context));
            case NOUN -> new ExpressionConstantNoun(expression.getValueNoun(context));
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

}
