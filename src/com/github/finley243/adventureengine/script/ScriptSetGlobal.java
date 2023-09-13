package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.expression.Expression;

public class ScriptSetGlobal extends Script {

    private final Expression globalID;
    private final Expression expression;

    public ScriptSetGlobal(Condition condition, Expression globalID, Expression expression) {
        super(condition);
        if (globalID == null) throw new IllegalArgumentException("ScriptSetGlobal globalID is null");
        if (globalID.getDataType() != Expression.DataType.STRING) throw new IllegalArgumentException("ScriptSetGlobal globalID is not a string");
        this.globalID = globalID;
        this.expression = expression;
    }

    @Override
    protected void executeSuccess(Context context) {
        String globalIDValue = globalID.getValueString(context);
        switch (expression.getDataType()) {
            case BOOLEAN -> context.game().data().setGlobalBoolean(globalIDValue, expression.getValueBoolean(context));
            case INTEGER -> context.game().data().setGlobalInteger(globalIDValue, expression.getValueInteger(context));
            case FLOAT -> context.game().data().setGlobalFloat(globalIDValue, expression.getValueFloat(context));
            case STRING -> context.game().data().setGlobalString(globalIDValue, expression.getValueString(context));
            case STRING_SET -> context.game().data().setGlobalStringSet(globalIDValue, expression.getValueStringSet(context));
            default ->
                    throw new UnsupportedOperationException("No globals for provided data type: " + expression.getDataType());
        }
    }

}
