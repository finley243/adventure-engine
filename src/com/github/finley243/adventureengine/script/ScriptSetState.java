package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.stat.StatHolderReference;

public class ScriptSetState extends Script {

    private final StatHolderReference holder;
    private final Expression state;
    private final Expression expression;

    public ScriptSetState(StatHolderReference holder, Expression state, Expression expression) {
        if (holder == null) throw new IllegalArgumentException("ScriptSetState stat holder is null");
        if (state == null) throw new IllegalArgumentException("ScriptSetState state name is null");
        this.holder = holder;
        this.state = state;
        this.expression = expression;
    }

    @Override
    public ScriptReturnData execute(Context context) {
        if (state.getDataType(context) != Expression.DataType.STRING) throw new IllegalArgumentException("ScriptSetState state name is not a string");
        String stateValue = state.getValueString(context);
        boolean success = holder.getHolder(context).setStatValue(stateValue, expression, context);
        if (!success) {
            context.game().log().print("ScriptSetState - stat " + stateValue + " on holder " + holder + " was not updated successfully");
        }
        return new ScriptReturnData(null, false, false, null);
    }

}
