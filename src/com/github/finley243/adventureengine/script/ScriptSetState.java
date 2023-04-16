package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.ContextScript;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.stat.StatHolderReference;
import com.github.finley243.adventureengine.variable.Variable;

import java.util.Map;

public class ScriptSetState extends Script {

    private final StatHolderReference holder;
    private final String state;
    private final Variable variable;

    public ScriptSetState(Condition condition, Map<String, Variable> localParameters, StatHolderReference holder, String state, Variable variable) {
        super(condition, localParameters);
        this.holder = holder;
        this.state = state;
        this.variable = variable;
    }

    @Override
    protected void executeSuccess(ContextScript context) {
        switch (variable.getDataType()) {
            case BOOLEAN -> holder.getHolder(context).setStateBoolean(state, variable.getValueBoolean(context));
            case INTEGER -> holder.getHolder(context).setStateInteger(state, variable.getValueInteger(context));
            case FLOAT -> holder.getHolder(context).setStateFloat(state, variable.getValueFloat(context));
            case STRING -> holder.getHolder(context).setStateString(state, variable.getValueString(context));
            case STRING_SET -> holder.getHolder(context).setStateStringSet(state, variable.getValueStringSet(context));
        }
    }

}
