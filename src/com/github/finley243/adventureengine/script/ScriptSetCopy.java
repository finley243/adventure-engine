package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;

import java.util.HashSet;
import java.util.Set;

public class ScriptSetCopy extends Script {

    public ScriptSetCopy(int line) {
        super(line);
    }

    @Override
    public ScriptReturnData execute(Context context) {
        Expression setExpression = context.getLocalVariables().get("set").getExpression();
        if (setExpression.getDataType() != Expression.DataType.SET) return new ScriptReturnData(null, null, new ScriptErrorData("Set parameter is not a set", getLine()));
        Set<Expression> set = setExpression.getValueSet();
        Set<Expression> setCopy = new HashSet<>(set);
        return new ScriptReturnData(Expression.constant(setCopy), FlowStatementType.RETURN, null);
    }

}
