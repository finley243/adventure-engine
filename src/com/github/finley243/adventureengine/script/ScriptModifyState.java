package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.ContextScript;
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
    protected void executeSuccess(ContextScript context) {
        switch (variable.getDataType()) {
            case INTEGER -> holder.getHolder(context).modStateInteger(state, variable.getValueInteger(context));
            case FLOAT -> holder.getHolder(context).modStateFloat(state, variable.getValueFloat(context));
            default ->
                    throw new UnsupportedOperationException("No modify functions for provided data type: " + variable.getDataType());
        }
    }

}
