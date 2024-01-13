package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;

import java.util.List;

public class ScriptAnd extends Script {

    private final List<Script> subScripts;

    public ScriptAnd(List<Script> subScripts) {
        this.subScripts = subScripts;
    }

    @Override
    public ScriptReturnData execute(Context context) {
        for (Script subScript : subScripts) {
            ScriptReturnData result = subScript.execute(context);
            if (result.error() != null) {
                return result;
            } else if (result.isReturn()) {
                return new ScriptReturnData(null, false, false, "Expression cannot contain a return statement");
            } else if (result.value() == null) {
                return new ScriptReturnData(null, false, false, "Expression received a null value");
            } else if (result.value().getDataType(context) != Expression.DataType.BOOLEAN) {
                return new ScriptReturnData(null, false, false, "Expression expected a boolean value");
            } else if (!result.value().getValueBoolean(context)) {
                return new ScriptReturnData(Expression.constant(false), false, false, null);
            }
        }
        return new ScriptReturnData(Expression.constant(true), false, false, null);
    }

}
