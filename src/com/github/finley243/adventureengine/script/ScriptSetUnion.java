package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;
import com.google.common.collect.Sets;

import java.util.HashSet;
import java.util.Set;

public class ScriptSetUnion extends Script {

    @Override
    public ScriptReturnData execute(Context context) {
        Expression setOneExpression = context.getLocalVariables().get("setOne").getExpression();
        Expression setTwoExpression = context.getLocalVariables().get("setTwo").getExpression();
        if (setOneExpression.getDataType() != Expression.DataType.SET) return new ScriptReturnData(null, null, "Set one parameter is not a set");
        if (setTwoExpression.getDataType() != Expression.DataType.SET) return new ScriptReturnData(null, null, "Set two parameter is not a set");
        Set<Expression> setOne = setOneExpression.getValueSet();
        Set<Expression> setTwo = setTwoExpression.getValueSet();
        Set<Expression> setUnion = new HashSet<>();
        setUnion.addAll(setOne);
        setUnion.addAll(setTwo);
        return new ScriptReturnData(Expression.constant(setUnion), FlowStatementType.RETURN, null);
    }

}
