package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.expression.Expression;

public class ScriptReturn extends Script {

    private final Expression expressionReturn;

    public ScriptReturn(Expression expressionReturn) {
        this.expressionReturn = expressionReturn;
    }

    @Override
    public void execute(RuntimeStack runtimeStack) {
        sendReturn(runtimeStack, new ScriptReturnData(expressionReturn, true, false, null));
    }

}
