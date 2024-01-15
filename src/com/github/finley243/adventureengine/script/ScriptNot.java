package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;

public class ScriptNot extends Script {

    private final Script subScript;

    public ScriptNot(Script subScript) {
        this.subScript = subScript;
    }

    @Override
    public ScriptReturnData execute(Context context) {
        ScriptReturnData scriptResult = subScript.execute(context);
        if (scriptResult.error() != null) {
            return scriptResult;
        } else if (scriptResult.isReturn()) {
            return new ScriptReturnData(null, false, false, "Expression cannot contain a return statement");
        } else if (scriptResult.value() == null) {
            return new ScriptReturnData(null, false, false, "Expression did not receive a value");
        } else if (scriptResult.value().getDataType() != Expression.DataType.BOOLEAN) {
            return new ScriptReturnData(null, false, false, "Expression expected a boolean value");
        }
        return new ScriptReturnData(Expression.constant(!scriptResult.value().getValueBoolean()), false, false, null);
    }

}
