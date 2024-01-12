package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.expression.Expression;

public class ScriptExpression extends Script {

    private final Expression expression;

    public ScriptExpression(Expression expression) {
        this.expression = expression;
    }

    @Override
    public void execute(RuntimeStack runtimeStack) {
        // TODO - Restrict usage of ScriptExpression to expressions that will already be constant (variables, constants, game values, etc.)
        sendReturn(runtimeStack, new ScriptReturnData(Expression.convertToConstant(expression, runtimeStack.getContext()), false, false, null));
    }

}
