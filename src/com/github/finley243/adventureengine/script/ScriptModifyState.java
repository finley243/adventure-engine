package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.stat.StatHolderReference;
import com.github.finley243.adventureengine.variable.Variable;

public class ScriptModifyState extends Script {

    private final StatHolderReference holder;
    private final String state;
    private final Variable variable;

    public ScriptModifyState(Condition condition, StatHolderReference holder, String state, Variable variable) {
        super(condition);
        this.holder = holder;
        this.state = state;
        this.variable = variable;
    }

    @Override
    protected void executeSuccess(Actor subject, Actor target) {
        switch (variable.getDataType()) {
            case INTEGER:
                holder.getHolder(subject.game(), subject, target).modStateInteger(state, variable.getValueInteger(subject.game(), subject, target));
                break;
            case FLOAT:
                holder.getHolder(subject.game(), subject, target).modStateFloat(state, variable.getValueFloat(subject.game(), subject, target));
                break;
            default:
                throw new UnsupportedOperationException("No modify functions for provided data type: " + variable.getDataType());
        }
    }

}
