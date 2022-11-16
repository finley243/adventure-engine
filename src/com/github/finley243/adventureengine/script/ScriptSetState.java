package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.stat.StatHolderReference;
import com.github.finley243.adventureengine.variable.Variable;

public class ScriptSetState extends Script {

    private final StatHolderReference holder;
    private final String state;
    private final Variable variable;

    public ScriptSetState(Condition condition, StatHolderReference holder, String state, Variable variable) {
        super(condition);
        this.holder = holder;
        this.state = state;
        this.variable = variable;
    }

    @Override
    protected void executeSuccess(Actor subject, Actor target) {
        switch (variable.getDataType()) {
            case BOOLEAN:
                holder.getHolder(subject.game(), subject, target).setStateBoolean(state, variable.getValueBoolean(subject.game(), subject, target));
                break;
            case INTEGER:
                holder.getHolder(subject.game(), subject, target).setStateInteger(state, variable.getValueInteger(subject.game(), subject, target));
                break;
            case FLOAT:
                holder.getHolder(subject.game(), subject, target).setStateFloat(state, variable.getValueFloat(subject.game(), subject, target));
                break;
            case STRING:
                holder.getHolder(subject.game(), subject, target).setStateString(state, variable.getValueString(subject.game(), subject, target));
                break;
            case STRING_SET:
                holder.getHolder(subject.game(), subject, target).setStateStringSet(state, variable.getValueStringSet(subject.game(), subject, target));
                break;
        }
    }

}
