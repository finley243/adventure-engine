package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;

public class ScriptNot extends Script {

    private final Script subScript;

    public ScriptNot(int line, Script subScript) {
        super(line);
        this.subScript = subScript;
    }

    @Override
    public ScriptReturnData execute(Context context) {
        ScriptReturnData scriptResult = subScript.execute(context);
        if (scriptResult.error() != null) {
            return scriptResult;
        } else if (scriptResult.flowStatement() != null) {
            return new ScriptReturnData(null, null, new ScriptErrorData("Expression cannot contain a flow statement", getLine()));
        } else if (scriptResult.value() == null) {
            return new ScriptReturnData(null, null, new ScriptErrorData("Expression received a null value", getLine()));
        } else if (scriptResult.value().getDataType() != Expression.DataType.BOOLEAN) {
            return new ScriptReturnData(null, null, new ScriptErrorData("Expression expected a boolean value", getLine()));
        }
        return new ScriptReturnData(Expression.constant(!scriptResult.value().getValueBoolean()), null, null);
    }

}
