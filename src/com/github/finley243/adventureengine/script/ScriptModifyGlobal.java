package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.expression.Expression;

public class ScriptModifyGlobal extends Script {

    private final Expression globalID;
    private final Expression expression;

    public ScriptModifyGlobal(Condition condition, Expression globalID, Expression expression) {
        super(condition);
        if (globalID == null) throw new IllegalArgumentException("ScriptModifyGlobal globalID is null");
        this.globalID = globalID;
        this.expression = expression;
    }

    @Override
    protected void executeSuccess(Context context) {
        if (globalID.getDataType(context) != Expression.DataType.STRING) throw new IllegalArgumentException("ScriptModifyGlobal globalID is not a string");
        String globalIDValue = globalID.getValueString(context);
        switch (expression.getDataType(context)) {
            case INTEGER -> {
                int oldValueInt = context.game().data().getGlobalInteger(globalIDValue);
                context.game().data().setGlobalInteger(globalIDValue, oldValueInt + expression.getValueInteger(context));
            }
            case FLOAT -> {
                float oldValueFloat = context.game().data().getGlobalFloat(globalIDValue);
                context.game().data().setGlobalFloat(globalIDValue, oldValueFloat + expression.getValueFloat(context));
            }
            default ->
                    throw new UnsupportedOperationException("No modify functions for provided data type: " + expression.getDataType(context));
        }
    }

}
