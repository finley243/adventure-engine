package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;

public class ScriptExpression extends Script {

    private final Expression expression;

    public ScriptExpression(int line, Expression expression) {
        super(line);
        this.expression = expression;
    }

    @Override
    public ScriptReturnData execute(Context context) {
        return new ScriptReturnData(expression, null, null);
    }

}
