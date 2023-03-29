package com.github.finley243.adventureengine.variable;

import com.github.finley243.adventureengine.ContextScript;
import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.textgen.Noun;

import java.util.Set;

public abstract class Variable {

    public enum DataType {
        BOOLEAN, INTEGER, FLOAT, STRING, STRING_SET, INVENTORY, NOUN
    }

    public abstract DataType getDataType();

    public boolean getValueBoolean(ContextScript context) {
        throw new UnsupportedOperationException("Invalid data type function: boolean");
    }

    public int getValueInteger(ContextScript context) {
        throw new UnsupportedOperationException("Invalid data type function: integer");
    }

    public float getValueFloat(ContextScript context) {
        throw new UnsupportedOperationException("Invalid data type function: float");
    }

    public String getValueString(ContextScript context) {
        throw new UnsupportedOperationException("Invalid data type function: string");
    }

    public Set<String> getValueStringSet(ContextScript context) {
        throw new UnsupportedOperationException("Invalid data type function: string set");
    }

    public Inventory getValueInventory(ContextScript context) {
        throw new UnsupportedOperationException("Invalid data type function: inventory");
    }

    public Noun getValueNoun(ContextScript context) {
        throw new UnsupportedOperationException("Invalid data type function: noun");
    }

    public boolean canCompareTo(Variable other) {
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

}
