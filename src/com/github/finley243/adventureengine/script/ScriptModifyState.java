package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.stat.StatHolderReference;
import com.github.finley243.adventureengine.expression.Expression;

import java.util.Map;

public class ScriptModifyState extends Script {

    private final StatHolderReference holder;
    private final String state;
    private final Expression expression;

    public ScriptModifyState(Condition condition, Map<String, Expression> localParameters, StatHolderReference holder, String state, Expression expression) {
        super(condition, localParameters);
        this.holder = holder;
        this.state = state;
        this.expression = expression;
    }

    @Override
    protected void executeSuccess(Context context) {
        switch (expression.getDataType()) {
            case INTEGER -> holder.getHolder(context).modStateInteger(state, expression.getValueInteger(context));
            case FLOAT -> holder.getHolder(context).modStateFloat(state, expression.getValueFloat(context));
            default ->
                    throw new UnsupportedOperationException("No modify functions for provided data type: " + expression.getDataType());
        }
    }

}
