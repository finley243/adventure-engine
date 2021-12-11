package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Data;
import com.github.finley243.adventureengine.actor.Actor;

public class ScriptVariableSet implements Script {

    private final String variableID;
    private final int value;

    public ScriptVariableSet(String variableID, int value) {
        this.variableID = variableID;
        this.value = value;
    }

    @Override
    public void execute(Actor subject) {
        Data.setVariable(variableID, value);
    }

}
