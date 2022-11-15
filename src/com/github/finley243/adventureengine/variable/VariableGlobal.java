package com.github.finley243.adventureengine.variable;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;

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
    public boolean getValueBoolean(Game game, Actor subject, Actor target) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getValueInteger(Game game, Actor subject, Actor target) {
        return game.data().getVariable(variableID);
    }

    @Override
    public float getValueFloat(Game game, Actor subject, Actor target) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getValueString(Game game, Actor subject, Actor target) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<String> getValueStringSet(Game game, Actor subject, Actor target) {
        throw new UnsupportedOperationException();
    }

}
