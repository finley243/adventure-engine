package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;

public class ScriptToString extends Script {

    @Override
    public ScriptReturnData execute(Context context) {
        Expression valueExpression = context.getLocalVariables().get("value").getExpression();
        String stringValue = switch (valueExpression.getDataType()) {
            case BOOLEAN -> Boolean.toString(valueExpression.getValueBoolean());
            case INTEGER -> Integer.toString(valueExpression.getValueInteger());
            case FLOAT -> Float.toString(valueExpression.getValueFloat());
            case STRING -> valueExpression.getValueString();
            case STRING_SET -> valueExpression.getValueStringSet().toString();
            case NOUN -> valueExpression.getValueNoun().getName();
            case INVENTORY, STAT_HOLDER -> null;
        };
        if (stringValue == null) return new ScriptReturnData(null, null, "Value parameter cannot be converted to string");
        return new ScriptReturnData(Expression.constant(stringValue), null, null);
    }

}
