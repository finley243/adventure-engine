package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.stat.StatHolderReference;
import com.github.finley243.adventureengine.expression.Expression;

import java.util.Map;

public class ScriptSetState extends Script {

    private final StatHolderReference holder;
    private final Expression state;
    private final Expression expression;

    public ScriptSetState(Condition condition, Map<String, Expression> localParameters, StatHolderReference holder, Expression state, Expression expression) {
        super(condition, localParameters);
        if (holder == null) throw new IllegalArgumentException("ScriptSetState stat holder is null");
        if (state == null) throw new IllegalArgumentException("ScriptSetState state name is null");
        if (state.getDataType() != Expression.DataType.STRING) throw new IllegalArgumentException("ScriptSetState state name is not a string");
        this.holder = holder;
        this.state = state;
        this.expression = expression;
    }

    @Override
    protected void executeSuccess(Context context) {
        String stateValue = state.getValueString(context);
        switch (expression.getDataType()) {
            case BOOLEAN -> holder.getHolder(context).setStateBoolean(stateValue, expression.getValueBoolean(context));
            case INTEGER -> holder.getHolder(context).setStateInteger(stateValue, expression.getValueInteger(context));
            case FLOAT -> holder.getHolder(context).setStateFloat(stateValue, expression.getValueFloat(context));
            case STRING -> holder.getHolder(context).setStateString(stateValue, expression.getValueString(context));
            case STRING_SET -> holder.getHolder(context).setStateStringSet(stateValue, expression.getValueStringSet(context));
        }
    }

}
