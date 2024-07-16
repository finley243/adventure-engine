package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.component.EffectComponent;
import com.github.finley243.adventureengine.effect.Effectible;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.stat.MutableStatHolder;

public class ScriptEffectAdd extends Script{

    public ScriptEffectAdd(ScriptTraceData traceData) {
        super(traceData);
    }

    @Override
    public ScriptReturnData execute(Context context) {
        Expression targetExpression = context.getLocalVariables().get("target").getExpression();
        Expression effectExpression = context.getLocalVariables().get("effect").getExpression();
        if (targetExpression.getDataType() != Expression.DataType.STAT_HOLDER) return new ScriptReturnData(null, null, new ScriptErrorData("Target parameter is not a stat holder", getTraceData()));
        if (!(targetExpression.getValueStatHolder() instanceof Effectible effectibleTarget)) return new ScriptReturnData(null, null, new ScriptErrorData("Target parameter does not support effects", getTraceData()));
        if (targetExpression.getDataType() != Expression.DataType.STRING) return new ScriptReturnData(null, null, new ScriptErrorData("Effect parameter is not a string", getTraceData()));
        String effectID = effectExpression.getValueString();
        effectibleTarget.addEffect(effectID);
        return new ScriptReturnData(null, null, null);
    }

}
