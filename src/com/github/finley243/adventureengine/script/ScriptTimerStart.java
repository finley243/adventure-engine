package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.Timer;
import com.github.finley243.adventureengine.expression.Expression;

public class ScriptTimerStart extends Script {

    private final Expression timerID;
    private final Expression timerDuration;
    private final Script timerScriptExpire;
    private final Script timerScriptUpdate;

    public ScriptTimerStart(Expression timerID, Expression timerDuration, Script timerScriptExpire, Script timerScriptUpdate) {
        if (timerID == null) throw new IllegalArgumentException("ScriptTimerStart timerID is null");
        if (timerDuration == null) throw new IllegalArgumentException("ScriptTimerStart timerDuration is null");
        this.timerID = timerID;
        this.timerDuration = timerDuration;
        this.timerScriptExpire = timerScriptExpire;
        this.timerScriptUpdate = timerScriptUpdate;
    }

    @Override
    public ScriptReturnData execute(Context context) {
        if (timerID.getDataType(context) != Expression.DataType.STRING) throw new IllegalArgumentException("ScriptTimerStart timerID is not a string");
        if (timerDuration.getDataType(context) != Expression.DataType.INTEGER) throw new IllegalArgumentException("ScriptTimerStart timerDuration is not an integer");
        Timer timer = new Timer(context.game(), timerID.getValueString(context), timerDuration.getValueInteger(context), timerScriptExpire, timerScriptUpdate, context);
        context.game().data().addTimer(timer.getID(), timer);
        return new ScriptReturnData(null, false, false, null);
    }

}
