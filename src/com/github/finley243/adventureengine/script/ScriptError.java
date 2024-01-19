package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;

public class ScriptError extends Script {

    private final Script scriptMessage;

    public ScriptError(Script scriptMessage) {
        this.scriptMessage = scriptMessage;
    }

    @Override
    public ScriptReturnData execute(Context context) {
        ScriptReturnData messageResult = scriptMessage.execute(context);
        if (messageResult.error() != null) {
            return messageResult;
        } else if (messageResult.flowStatement() != null) {
            return new ScriptReturnData(null, null, "Expression cannot contain a flow statement");
        } else if (messageResult.value() == null) {
            return new ScriptReturnData(null, null, "Expression provided a null value");
        } else if (messageResult.value().getDataType() != Expression.DataType.STRING) {
            return new ScriptReturnData(null, null, "Expression provided a non-string value");
        }
        String messageValue = messageResult.value().getValueString();
        return new ScriptReturnData(null, null, messageValue);
    }

}
