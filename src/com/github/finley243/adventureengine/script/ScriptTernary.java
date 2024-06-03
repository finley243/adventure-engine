package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;

public class ScriptTernary extends Script {

    private final Script scriptCondition;
    private final Script scriptTrue;
    private final Script scriptFalse;

    public ScriptTernary(int line, Script scriptCondition, Script scriptTrue, Script scriptFalse) {
        super(line);
        this.scriptCondition = scriptCondition;
        this.scriptTrue = scriptTrue;
        this.scriptFalse = scriptFalse;
    }

    @Override
    public ScriptReturnData execute(Context context) {
        ScriptReturnData conditionResult = scriptCondition.execute(context);
        if (conditionResult.error() != null) {
            return conditionResult;
        } else if (conditionResult.flowStatement() != null) {
            return new ScriptReturnData(null, null, new ScriptErrorData("Ternary condition contains an unexpected flow statement", getLine()));
        } else if (conditionResult.value() == null) {
            return new ScriptReturnData(null, null, new ScriptErrorData("Ternary condition provided a null value", getLine()));
        } else if (conditionResult.value().getDataType() != Expression.DataType.BOOLEAN) {
            return new ScriptReturnData(null, null, new ScriptErrorData("Ternary condition provided a non-boolean value", getLine()));
        }
        boolean conditionSuccess = conditionResult.value().getValueBoolean();
        if (conditionSuccess) {
            return scriptTrue.execute(context);
        } else {
            return scriptFalse.execute(context);
        }
    }

}
