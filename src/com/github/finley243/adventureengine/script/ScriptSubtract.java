package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;

public class ScriptSubtract extends Script implements ScriptReturnTarget {

    private final Script firstScript;
    private final Script secondScript;

    public ScriptSubtract(Script firstScript, Script secondScript) {
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
            if (canSubtractExpressions(firstScriptValue, secondScriptValue, runtimeStack.getContext())) {
                Expression subtractResult = subtractExpressions(firstScriptValue, secondScriptValue, runtimeStack.getContext());
                runtimeStack.closeContext();
                sendReturn(runtimeStack, new ScriptReturnData(subtractResult, false, false, null));
            } else {
                runtimeStack.closeContext();
                sendReturn(runtimeStack, new ScriptReturnData(null, false, false, "Expression received values that could not be subtracted"));
            }
        } else {
            Expression firstScriptValue = scriptReturnData.value();
            runtimeStack.addTempExpressionToList(firstScriptValue);
            secondScript.execute(runtimeStack);
        }
    }

    private boolean canSubtractExpressions(Expression firstExpression, Expression secondExpression, Context context) {
        if (firstExpression.getDataType(context) == Expression.DataType.INTEGER || firstExpression.getDataType(context) == Expression.DataType.FLOAT) {
            return secondExpression.getDataType(context) == Expression.DataType.INTEGER || secondExpression.getDataType(context) == Expression.DataType.FLOAT;
        }
        return false;
    }

    private Expression subtractExpressions(Expression firstExpression, Expression secondExpression, Context context) {
        if (firstExpression.getDataType(context) == Expression.DataType.INTEGER && secondExpression.getDataType(context) == Expression.DataType.INTEGER) {
            int value1 = firstExpression.getValueInteger(context);
            int value2 = secondExpression.getValueInteger(context);
            return Expression.constant(value1 - value2);
        } else if ((firstExpression.getDataType(context) == Expression.DataType.INTEGER || firstExpression.getDataType(context) == Expression.DataType.FLOAT)
                && (secondExpression.getDataType(context) == Expression.DataType.INTEGER || secondExpression.getDataType(context) == Expression.DataType.FLOAT)) {
            float value1 = firstExpression.getDataType(context) == Expression.DataType.INTEGER ? firstExpression.getValueInteger(context) : firstExpression.getValueFloat(context);
            float value2 = secondExpression.getDataType(context) == Expression.DataType.INTEGER ? secondExpression.getValueInteger(context) : secondExpression.getValueFloat(context);
            return Expression.constant(value1 - value2);
        }
        return null;
    }

}
