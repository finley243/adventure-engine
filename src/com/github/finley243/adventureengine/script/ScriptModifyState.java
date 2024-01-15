package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.stat.StatHolderReference;

public class ScriptModifyState extends Script {

    private final StatHolderReference holder;
    private final Expression state;
    private final Expression expression;

    public ScriptModifyState(StatHolderReference holder, Expression state, Expression expression) {
        if (holder == null) throw new IllegalArgumentException("ScriptModifyState stat holder is null");
        if (state == null) throw new IllegalArgumentException("ScriptModifyState state name is null");
        this.holder = holder;
        this.state = state;
        this.expression = expression;
    }

    @Override
    public ScriptReturnData execute(Context context) {
        if (state.getDataType() != Expression.DataType.STRING) throw new IllegalArgumentException("ScriptModifyState state name is not a string");
        String stateValue = state.getValueString();
        switch (expression.getDataType()) {
            case INTEGER -> {
                Expression oldValueExpression = holder.getHolder(context).getStatValue(stateValue, context);
                if (oldValueExpression == null) throw new UnsupportedOperationException("Expression " + stateValue + " does not exist on holder");
                if (oldValueExpression.getDataType() != Expression.DataType.INTEGER) throw new UnsupportedOperationException("Expression " + stateValue + " is not a float");
                int oldValue = oldValueExpression.getValueInteger();
                Expression newValueExpression = Expression.constant(oldValue + expression.getValueInteger());
                holder.getHolder(context).setStatValue(stateValue, newValueExpression, context);
            }
            case FLOAT -> {
                Expression oldValueExpression = holder.getHolder(context).getStatValue(stateValue, context);
                if (oldValueExpression == null) throw new UnsupportedOperationException("Expression " + stateValue + " does not exist on holder");
                if (oldValueExpression.getDataType() != Expression.DataType.FLOAT) throw new UnsupportedOperationException("Expression " + stateValue + " is not a float");
                float oldValue = oldValueExpression.getValueFloat();
                Expression newValueExpression = Expression.constant(oldValue + expression.getValueFloat());
                holder.getHolder(context).setStatValue(stateValue, newValueExpression, context);
            }
            default ->
                    throw new UnsupportedOperationException("No modify functions for provided data type: " + expression.getDataType());
        }
        return new ScriptReturnData(null, false, false, null);
    }

}
