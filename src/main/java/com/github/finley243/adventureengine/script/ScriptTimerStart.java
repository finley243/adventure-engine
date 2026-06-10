package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.Timer;
import com.github.finley243.adventureengine.expression.Expression;

public class ScriptTimerStart extends Script {

    public ScriptTimerStart(ScriptTraceData traceData) {
        super(traceData);
    }

    @Override
    public ScriptReturnData execute(Context context) {
        Expression timerID = context.getLocalVariables().get("timer").getExpression();
        Expression timerDuration = context.getLocalVariables().get("duration").getExpression();
        Expression timerScriptExpire = context.getLocalVariables().get("scriptExpire").getExpression();
        Expression timerScriptUpdate = context.getLocalVariables().get("scriptUpdate").getExpression();
        if (timerID.getDataType() != Expression.DataType.STRING) return new ScriptReturnData(null, null, new ScriptErrorData("ScriptTimerStart timerID is not a string", getTraceData()));
        if (timerDuration.getDataType() != Expression.DataType.INTEGER) return new ScriptReturnData(null, null, new ScriptErrorData("ScriptTimerStart timerDuration is not an integer", getTraceData()));
        if (timerScriptExpire != null && timerScriptExpire.getDataType() != Expression.DataType.STRING) return new ScriptReturnData(null, null, new ScriptErrorData("ScriptTimerStart scriptExpire is not a string", getTraceData()));
        if (timerScriptUpdate != null && timerScriptUpdate.getDataType() != Expression.DataType.STRING) return new ScriptReturnData(null, null, new ScriptErrorData("ScriptTimerStart scriptUpdate is not a string", getTraceData()));
        Script scriptExpire = null;
        Script scriptUpdate = null;
        if (timerScriptExpire != null) {
            String scriptExpireID = timerScriptExpire.getValueString();
            scriptExpire = context.game().data().getScript(scriptExpireID).script();
        }
        if (timerScriptUpdate != null) {
            String scriptUpdateID = timerScriptUpdate.getValueString();
            scriptUpdate = context.game().data().getScript(scriptUpdateID).script();
        }
        Timer timer = new Timer(context.game(), timerID.getValueString(), timerDuration.getValueInteger(), scriptExpire, scriptUpdate, context);
        context.game().data().addTimer(timer.getID(), timer);
        return new ScriptReturnData(null, null, null);
    }

}
