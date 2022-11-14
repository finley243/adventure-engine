package com.github.finley243.adventureengine.variable;

import com.github.finley243.adventureengine.Game;

import java.util.Set;

public class VariableGlobal extends Variable {

    private final String variableID;

    public VariableGlobal(String variableID) {
        this.variableID = variableID;
    }

    @Override
    public DataType getDataType() {
        return DataType.INTEGER;
    }

    @Override
    public boolean getValueBoolean(Game game) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getValueInteger(Game game) {
        return game.data().getVariable(variableID);
    }

    @Override
    public float getValueFloat(Game game) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getValueString(Game game) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<String> getValueStringSet(Game game) {
        throw new UnsupportedOperationException();
    }

}
