package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;

public class ScriptAdd extends Script implements ScriptReturnTarget {

    private final Script firstScript;
    private final Script secondScript;

    public ScriptAdd(Script firstScript, Script secondScript) {
        this.firstScript = firstScript;
        this.secondScript = secondScript;
    }

    @Override
    public void execute(RuntimeStack runtimeStack) {
        runtimeStack.addContext(runtimeStack.getContext(), this);
        firstScript.execute(runtimeStack);
    }

    @Override
    public void onScriptReturn(RuntimeStack runtimeStack, ScriptReturnData scriptReturnData) {
        if (scriptReturnData.error() != null) {
            runtimeStack.closeContext();
            sendReturn(runtimeStack, scriptReturnData);
        } else if (scriptReturnData.isReturn()) {
            runtimeStack.closeContext();
            sendReturn(runtimeStack, new ScriptReturnData(null, false, false, "Expression cannot contain a return statement"));
        } else if (scriptReturnData.value() == null) {
            runtimeStack.closeContext();
            sendReturn(runtimeStack, new ScriptReturnData(null, false, false, "Expression did not receive a value"));
        } else if (runtimeStack.getTempExpressionList().size() == 1) {
            Expression firstScriptValue = runtimeStack.getTempExpressionList().getFirst();
            Expression secondScriptValue = scriptReturnData.value();
            if (canAddExpressions(firstScriptValue, secondScriptValue, runtimeStack.getContext())) {
                Expression addResult = addExpressions(firstScriptValue, secondScriptValue, runtimeStack.getContext());
                runtimeStack.closeContext();
                sendReturn(runtimeStack, new ScriptReturnData(addResult, false, false, null));
            } else {
                runtimeStack.closeContext();
                sendReturn(runtimeStack, new ScriptReturnData(null, false, false, "Expression received values that could not be added"));
            }
        } else {
            Expression firstScriptValue = scriptReturnData.value();
            runtimeStack.addTempExpressionToList(firstScriptValue);
            secondScript.execute(runtimeStack);
        }
    }

    private boolean canAddExpressions(Expression firstExpression, Expression secondExpression, Context context) {
        if (firstExpression.getDataType(context) == Expression.DataType.INTEGER || firstExpression.getDataType(context) == Expression.DataType.FLOAT) {
            return secondExpression.getDataType(context) == Expression.DataType.INTEGER || secondExpression.getDataType(context) == Expression.DataType.FLOAT;
        } else if (firstExpression.getDataType(context) == Expression.DataType.STRING) {
            return secondExpression.getDataType(context) == Expression.DataType.STRING;
        }
        return false;
    }

    private Expression addExpressions(Expression firstExpression, Expression secondExpression, Context context) {
        if (firstExpression.getDataType(context) == Expression.DataType.INTEGER && secondExpression.getDataType(context) == Expression.DataType.INTEGER) {
            int value1 = firstExpression.getValueInteger(context);
            int value2 = secondExpression.getValueInteger(context);
            return Expression.constant(value1 + value2);
        } else if ((firstExpression.getDataType(context) == Expression.DataType.INTEGER || firstExpression.getDataType(context) == Expression.DataType.FLOAT)
                && (secondExpression.getDataType(context) == Expression.DataType.INTEGER || secondExpression.getDataType(context) == Expression.DataType.FLOAT)) {
            float value1 = firstExpression.getDataType(context) == Expression.DataType.INTEGER ? firstExpression.getValueInteger(context) : firstExpression.getValueFloat(context);
            float value2 = secondExpression.getDataType(context) == Expression.DataType.INTEGER ? secondExpression.getValueInteger(context) : secondExpression.getValueFloat(context);
            return Expression.constant(value1 + value2);
        } else if (firstExpression.getDataType(context) == Expression.DataType.STRING && secondExpression.getDataType(context) == Expression.DataType.STRING) {
            String value1 = firstExpression.getValueString(context);
            String value2 = secondExpression.getValueString(context);
            return Expression.constant(value1 + value2);
        }
        return null;
    }

}
