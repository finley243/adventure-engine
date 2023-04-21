package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.ContextScript;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.expression.Expression;

import java.util.Map;

public class ScriptSetGlobal extends Script {

    private final String globalID;
    private final Expression expression;

    public ScriptSetGlobal(Condition condition, Map<String, Expression> localParameters, String globalID, Expression expression) {
        super(condition, localParameters);
        this.globalID = globalID;
        this.expression = expression;
    }

    @Override
    protected void executeSuccess(ContextScript context) {
        switch (expression.getDataType()) {
            case BOOLEAN -> context.game().data().setGlobalBoolean(globalID, expression.getValueBoolean(context));
            case INTEGER -> context.game().data().setGlobalInteger(globalID, expression.getValueInteger(context));
            case FLOAT -> context.game().data().setGlobalFloat(globalID, expression.getValueFloat(context));
            case STRING -> context.game().data().setGlobalString(globalID, expression.getValueString(context));
            case STRING_SET -> context.game().data().setGlobalStringSet(globalID, expression.getValueStringSet(context));
            default ->
                    throw new UnsupportedOperationException("No globals for provided data type: " + expression.getDataType());
        }
    }

}
