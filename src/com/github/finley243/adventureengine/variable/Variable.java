package com.github.finley243.adventureengine.variable;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;

import java.util.Set;

public abstract class Variable {

    public enum DataType {
        BOOLEAN, INTEGER, FLOAT, STRING, STRING_SET
    }

    public abstract DataType getDataType();

    public abstract boolean getValueBoolean(Game game, Actor subject, Actor target);

    public abstract int getValueInteger(Game game, Actor subject, Actor target);

    public abstract float getValueFloat(Game game, Actor subject, Actor target);

    public abstract String getValueString(Game game, Actor subject, Actor target);

    public abstract Set<String> getValueStringSet(Game game, Actor subject, Actor target);

    public boolean canCompareTo(Variable other) {
        if (this.getDataType() == DataType.STRING_SET || other.getDataType() == DataType.STRING_SET) {
            return false;
        }
        if (this.getDataType() == DataType.INTEGER || this.getDataType() == DataType.FLOAT) {
            return other.getDataType() == DataType.INTEGER || other.getDataType() == DataType.FLOAT;
        }
        return this.getDataType() == other.getDataType();
    }

}
