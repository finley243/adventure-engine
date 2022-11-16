package com.github.finley243.adventureengine.variable;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;

import java.util.Set;

public class VariableGlobal extends Variable {

    private final String dataType;
    private final String variableID;

    public VariableGlobal(String dataType, String variableID) {
        this.dataType = dataType;
        this.variableID = variableID;
    }

    @Override
    public DataType getDataType() {
        switch (dataType) {
            case "boolean":
                return DataType.BOOLEAN;
            case "int":
                return DataType.INTEGER;
            case "float":
                return DataType.FLOAT;
            case "string":
                return DataType.STRING;
            case "stringSet":
                return DataType.STRING_SET;
            default:
                return null;
        }
    }

    @Override
    public boolean getValueBoolean(Game game, Actor subject, Actor target) {
        if (getDataType() != DataType.BOOLEAN) throw new UnsupportedOperationException();
        return game.data().getGlobalBoolean(variableID);
    }

    @Override
    public int getValueInteger(Game game, Actor subject, Actor target) {
        if (getDataType() != DataType.INTEGER) throw new UnsupportedOperationException();
        return game.data().getGlobalInteger(variableID);
    }

    @Override
    public float getValueFloat(Game game, Actor subject, Actor target) {
        if (getDataType() != DataType.FLOAT) throw new UnsupportedOperationException();
        return game.data().getGlobalFloat(variableID);
    }

    @Override
    public String getValueString(Game game, Actor subject, Actor target) {
        if (getDataType() != DataType.STRING) throw new UnsupportedOperationException();
        return game.data().getGlobalString(variableID);
    }

    @Override
    public Set<String> getValueStringSet(Game game, Actor subject, Actor target) {
        if (getDataType() != DataType.STRING_SET) throw new UnsupportedOperationException();
        return game.data().getGlobalStringSet(variableID);
    }

}
