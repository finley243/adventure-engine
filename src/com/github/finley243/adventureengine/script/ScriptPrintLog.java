package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;

public class ScriptPrintLog extends Script {

    private final Script scriptMessage;

    public ScriptPrintLog(Script scriptMessage) {
        this.scriptMessage = scriptMessage;
    }

    @Override
    public ScriptReturnData execute(Context context) {
        ScriptReturnData messageResult = scriptMessage.execute(context);
        if (messageResult.error() != null) {
            return messageResult;
        } else if (messageResult.flowStatement() != null) {
            return new ScriptReturnData(null, null, new ScriptErrorData("Expression cannot contain a flow statement", -1));
        } else if (messageResult.value() == null) {
            return new ScriptReturnData(null, null, new ScriptErrorData("Expression provided a null value", -1));
        } else if (messageResult.value().getDataType() != Expression.DataType.STRING) {
            return new ScriptReturnData(null, null, new ScriptErrorData("Expression provided a non-string value", -1));
        }
        String messageValue = messageResult.value().getValueString();
        context.game().log().print(messageValue);
        return new ScriptReturnData(null, null, null);
    }

}
