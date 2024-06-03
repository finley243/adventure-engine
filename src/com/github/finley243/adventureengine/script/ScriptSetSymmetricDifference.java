package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;

import java.util.HashSet;
import java.util.Set;

public class ScriptSetSymmetricDifference extends Script {

    public ScriptSetSymmetricDifference(int line) {
        super(line);
    }

    @Override
    public ScriptReturnData execute(Context context) {
        Expression setOneExpression = context.getLocalVariables().get("setOne").getExpression();
        Expression setTwoExpression = context.getLocalVariables().get("setTwo").getExpression();
        if (setOneExpression.getDataType() != Expression.DataType.SET) return new ScriptReturnData(null, null, new ScriptErrorData("Set one parameter is not a set", getLine()));
        if (setTwoExpression.getDataType() != Expression.DataType.SET) return new ScriptReturnData(null, null, new ScriptErrorData("Set two parameter is not a set", getLine()));
        Set<Expression> setOne = setOneExpression.getValueSet();
        Set<Expression> setTwo = setTwoExpression.getValueSet();
        Set<Expression> setSymmetricDifference = new HashSet<>();
        for (Expression expression : setOne) {
            if (!setTwo.contains(expression)) {
                setSymmetricDifference.add(expression);
            }
        }
        for (Expression expression : setTwo) {
            if (!setOne.contains(expression)) {
                setSymmetricDifference.add(expression);
            }
        }
        return new ScriptReturnData(Expression.constant(setSymmetricDifference), FlowStatementType.RETURN, null);
    }

}
