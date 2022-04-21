package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.condition.Condition;

public class ScriptVariableSet extends Script {

    private final String variableID;
    private final int value;

    public ScriptVariableSet(Condition condition, String variableID, int value) {
        super(condition);
        this.variableID = variableID;
        this.value = value;
    }

    @Override
    public void executeSuccess(Actor subject) {
        subject.game().data().setVariable(variableID, value);
    }

}
