package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.Timer;
import com.github.finley243.adventureengine.expression.Expression;

public class ScriptTimerStart extends Script {

    @Override
    public ScriptReturnData execute(Context context) {
        Expression timerID = context.getLocalVariables().get("timer").getExpression();
        Expression timerDuration = context.getLocalVariables().get("duration").getExpression();
        if (timerID.getDataType() != Expression.DataType.STRING) return new ScriptReturnData(null, null, "ScriptTimerStart timerID is not a string");
        if (timerDuration.getDataType() != Expression.DataType.INTEGER) return new ScriptReturnData(null, null, "ScriptTimerStart timerDuration is not an integer");
        // TODO - Design a way to define delayed scripts through timers in new script parser system
        Timer timer = new Timer(context.game(), timerID.getValueString(), timerDuration.getValueInteger(), null, null, context);
        context.game().data().addTimer(timer.getID(), timer);
        return new ScriptReturnData(null, null, null);
    }

}
