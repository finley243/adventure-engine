package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.effect.Effect;
import com.github.finley243.adventureengine.effect.Effectable;
import com.github.finley243.adventureengine.expression.Expression;

public class ScriptEffectRemove extends Script{

    public ScriptEffectRemove(ScriptTraceData traceData) {
        super(traceData);
    }

    @Override
    ScriptReturnData execute(ScriptRuntime scriptRuntime, Context context) {
        Expression targetExpression = context.getLocalVariables().get("target").getExpression();
        Expression effectExpression = context.getLocalVariables().get("effect").getExpression();
        if (targetExpression.getDataType() != Expression.DataType.STAT_HOLDER) return new ScriptReturnData(null, null, new ScriptErrorData("Target parameter is not a stat holder", getTraceData()));
        if (!(targetExpression.getValueStatHolder() instanceof Effectable effectableTarget)) return new ScriptReturnData(null, null, new ScriptErrorData("Target parameter does not support effects", getTraceData()));
        if (targetExpression.getDataType() != Expression.DataType.STRING) return new ScriptReturnData(null, null, new ScriptErrorData("Effect parameter is not a string", getTraceData()));
        String effectID = effectExpression.getValueString();
        Effect effect = scriptRuntime.getEffect(effectID);
        effectableTarget.removeEffect(effect);
        return new ScriptReturnData(null, null, null);
    }

}
