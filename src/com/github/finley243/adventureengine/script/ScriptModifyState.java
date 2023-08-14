package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.stat.StatHolderReference;
import com.github.finley243.adventureengine.expression.Expression;

import java.util.Map;

public class ScriptModifyState extends Script {

    private final StatHolderReference holder;
    private final Expression state;
    private final Expression expression;

    public ScriptModifyState(Condition condition, Map<String, Expression> localParameters, StatHolderReference holder, Expression state, Expression expression) {
        super(condition, localParameters);
        if (holder == null) throw new IllegalArgumentException("ScriptModifyState stat holder is null");
        if (state == null) throw new IllegalArgumentException("ScriptModifyState state name is null");
        if (state.getDataType() != Expression.DataType.STRING) throw new IllegalArgumentException("ScriptModifyState state name is not a string");
        this.holder = holder;
        this.state = state;
        this.expression = expression;
    }

    @Override
    protected void executeSuccess(Context context) {
        String stateValue = state.getValueString(context);
        switch (expression.getDataType()) {
            case INTEGER -> holder.getHolder(context).modStateInteger(stateValue, expression.getValueInteger(context));
            case FLOAT -> holder.getHolder(context).modStateFloat(stateValue, expression.getValueFloat(context));
            default ->
                    throw new UnsupportedOperationException("No modify functions for provided data type: " + expression.getDataType());
        }
        context.game().eventQueue().executeNext();
    }

}
