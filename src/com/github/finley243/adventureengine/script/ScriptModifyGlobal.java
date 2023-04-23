package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.expression.Expression;

import java.util.Map;

public class ScriptModifyGlobal extends Script {

    private final String globalID;
    private final Expression expression;

    public ScriptModifyGlobal(Condition condition, Map<String, Expression> localParameters, String globalID, Expression expression) {
        super(condition, localParameters);
        this.globalID = globalID;
        this.expression = expression;
    }

    @Override
    protected void executeSuccess(Context context) {
        switch (expression.getDataType()) {
            case INTEGER -> {
                int oldValueInt = context.game().data().getGlobalInteger(globalID);
                context.game().data().setGlobalInteger(globalID, oldValueInt + expression.getValueInteger(context));
            }
            case FLOAT -> {
                float oldValueFloat = context.game().data().getGlobalFloat(globalID);
                context.game().data().setGlobalFloat(globalID, oldValueFloat + expression.getValueFloat(context));
            }
            default ->
                    throw new UnsupportedOperationException("No modify functions for provided data type: " + expression.getDataType());
        }
    }

}
