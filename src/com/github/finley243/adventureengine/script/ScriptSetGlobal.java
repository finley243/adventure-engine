package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.variable.Variable;

public class ScriptSetGlobal extends Script {

    private final String globalID;
    private final Variable variable;

    public ScriptSetGlobal(Condition condition, String globalID, Variable variable) {
        super(condition);
        this.globalID = globalID;
        this.variable = variable;
    }

    @Override
    protected void executeSuccess(Actor subject, Actor target) {
        switch (variable.getDataType()) {
            case INTEGER:
                subject.game().data().setVariable(globalID, variable.getValueInteger(subject.game(), subject, target));
                break;
            default:
                throw new UnsupportedOperationException("No globals for provided data type: " + variable.getDataType());
        }
    }

}
