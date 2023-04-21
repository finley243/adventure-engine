package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.ContextScript;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.stat.StatHolderReference;
import com.github.finley243.adventureengine.expression.Expression;

import java.util.Map;

public class ScriptSetState extends Script {

    private final StatHolderReference holder;
    private final String state;
    private final Expression expression;

    public ScriptSetState(Condition condition, Map<String, Expression> localParameters, StatHolderReference holder, String state, Expression expression) {
        super(condition, localParameters);
        this.holder = holder;
        this.state = state;
        this.expression = expression;
    }

    @Override
    protected void executeSuccess(ContextScript context) {
        switch (expression.getDataType()) {
            case BOOLEAN -> holder.getHolder(context).setStateBoolean(state, expression.getValueBoolean(context));
            case INTEGER -> holder.getHolder(context).setStateInteger(state, expression.getValueInteger(context));
            case FLOAT -> holder.getHolder(context).setStateFloat(state, expression.getValueFloat(context));
            case STRING -> holder.getHolder(context).setStateString(state, expression.getValueString(context));
            case STRING_SET -> holder.getHolder(context).setStateStringSet(state, expression.getValueStringSet(context));
        }
    }

}
