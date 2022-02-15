package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Data;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.condition.Condition;

public class ScriptVariableMod extends Script {

    private final String variableID;
    private final int value;

    public ScriptVariableMod(Condition condition, String variableID, int value) {
        super(condition);
        this.variableID = variableID;
        this.value = value;
    }

    @Override
    public void execute(Actor subject) {
        if(canExecute(subject)) {
            int oldValue = Data.getVariable(variableID);
            Data.setVariable(variableID, oldValue + value);
        }
    }

}
