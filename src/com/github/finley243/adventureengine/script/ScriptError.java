package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;

public class ScriptError extends Script {

    private final Script scriptMessage;

    public ScriptError(int line, Script scriptMessage) {
        super(line);
        this.scriptMessage = scriptMessage;
    }

    @Override
    public ScriptReturnData execute(Context context) {
        ScriptReturnData messageResult = scriptMessage.execute(context);
        if (messageResult.error() != null) {
            return messageResult;
        } else if (messageResult.flowStatement() != null) {
            return new ScriptReturnData(null, null, new ScriptErrorData("Expression cannot contain a flow statement", getLine()));
        } else if (messageResult.value() == null) {
            return new ScriptReturnData(null, null, new ScriptErrorData("Expression provided a null value", getLine()));
        } else if (messageResult.value().getDataType() != Expression.DataType.STRING) {
            return new ScriptReturnData(null, null, new ScriptErrorData("Expression provided a non-string value", getLine()));
        }
        String messageValue = messageResult.value().getValueString();
        return new ScriptReturnData(null, null, new ScriptErrorData(messageValue, getLine()));
    }

}
