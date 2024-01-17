package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.expression.Expression;

public class ScriptTernary extends Script {

    private final Script scriptCondition;
    private final Script scriptTrue;
    private final Script scriptFalse;

    public ScriptTernary(Script scriptCondition, Script scriptTrue, Script scriptFalse) {
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
            return new ScriptReturnData(null, null, "Ternary condition contains an unexpected flow statement");
        } else if (conditionResult.value() == null) {
            return new ScriptReturnData(null, null, "Ternary condition provided a null value");
        } else if (conditionResult.value().getDataType() != Expression.DataType.BOOLEAN) {
            return new ScriptReturnData(null, null, "Ternary condition provided a non-boolean value");
        }
        boolean conditionSuccess = conditionResult.value().getValueBoolean();
        if (conditionSuccess) {
            return scriptTrue.execute(context);
        } else {
            return scriptFalse.execute(context);
        }
    }

}
