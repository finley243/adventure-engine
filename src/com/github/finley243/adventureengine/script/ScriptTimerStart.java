package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.Timer;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.expression.Expression;

import java.util.Map;

public class ScriptTimerStart extends Script {

    private final Expression timerID;
    private final Expression timerDuration;
    private final Script timerScriptExpire;
    private final Script timerScriptUpdate;

    public ScriptTimerStart(Condition condition, Map<String, Expression> localParameters, Expression timerID, Expression timerDuration, Script timerScriptExpire, Script timerScriptUpdate) {
        super(condition, localParameters);
        if (timerID == null) throw new IllegalArgumentException("ScriptTimerStart timerID is null");
        if (timerID.getDataType() != Expression.DataType.STRING) throw new IllegalArgumentException("ScriptTimerStart timerID is not a string");
        if (timerDuration == null) throw new IllegalArgumentException("ScriptTimerStart timerDuration is null");
        if (timerDuration.getDataType() != Expression.DataType.INTEGER) throw new IllegalArgumentException("ScriptTimerStart timerDuration is not an integer");
        this.timerID = timerID;
        this.timerDuration = timerDuration;
        this.timerScriptExpire = timerScriptExpire;
        this.timerScriptUpdate = timerScriptUpdate;
    }

    @Override
    protected void executeSuccess(Context context) {
        Timer timer = new Timer(context.game(), timerID.getValueString(context), timerDuration.getValueInteger(context), timerScriptExpire, timerScriptUpdate, context);
        context.game().data().addTimer(timer.getID(), timer);
    }

}
