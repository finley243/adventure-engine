package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;

import java.util.List;

public class ScriptOr extends Script {

    private final List<Script> subScripts;

    public ScriptOr(List<Script> subScripts) {
        this.subScripts = subScripts;
    }

    @Override
    public ScriptReturnData execute(Context context) {
        for (Script subScript : subScripts) {
            ScriptReturnData result = subScript.execute(context);
            if (result.error() != null) {
                System.out.println(subScript);
                return result;
            } else if (result.flowStatement() != null) {
                return new ScriptReturnData(null, null, "Expression cannot contain a flow statement");
            } else if (result.value() == null) {
                return new ScriptReturnData(null, null, "Expression received a null value");
            } else if (result.value().getDataType() != Expression.DataType.BOOLEAN) {
                return new ScriptReturnData(null, null, "Expression expected a boolean value");
            } else if (result.value().getValueBoolean()) {
                return new ScriptReturnData(Expression.constant(true), null, null);
            }
        }
        return new ScriptReturnData(Expression.constant(false), null, null);
    }

}
