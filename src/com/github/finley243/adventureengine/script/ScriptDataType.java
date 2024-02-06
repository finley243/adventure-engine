package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;

public class ScriptDataType extends Script {

    @Override
    public ScriptReturnData execute(Context context) {
        Expression valueExpression = context.getLocalVariables().get("value").getExpression();
        if (valueExpression == null) {
            return new ScriptReturnData(Expression.constant("null"), null, null);
        }
        String dataTypeName = switch (valueExpression.getDataType()) {
            case BOOLEAN -> "boolean";
            case INTEGER -> "integer";
            case FLOAT -> "float";
            case STRING -> "string";
            case SET -> "set";
            case LIST -> "list";
            case NOUN -> "noun";
            case INVENTORY -> "inventory";
            case STAT_HOLDER -> "statHolder";
        };
        return new ScriptReturnData(Expression.constant(dataTypeName), null, null);
    }

}
