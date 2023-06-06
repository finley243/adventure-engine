package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.Timer;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.expression.Expression;

import java.util.Map;

public class ScriptTimerStart extends Script {

    private final String timerID;
    private final int timerDuration;
    private final Script timerScriptExpire;
    private final Script timerScriptUpdate;

    public ScriptTimerStart(Condition condition, Map<String, Expression> localParameters, String timerID, int timerDuration, Script timerScriptExpire, Script timerScriptUpdate) {
        super(condition, localParameters);
        this.timerID = timerID;
        this.timerDuration = timerDuration;
        this.timerScriptExpire = timerScriptExpire;
        this.timerScriptUpdate = timerScriptUpdate;
    }

    @Override
    protected void executeSuccess(Context context) {
        Timer timer = new Timer(context.game(), timerID, timerDuration, timerScriptExpire, timerScriptUpdate, context);
        context.game().data().addTimer(timer.getID(), timer);
    }

}
