package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.ContextScript;
import com.github.finley243.adventureengine.Timer;
import com.github.finley243.adventureengine.condition.Condition;

public class ScriptTimerStart extends Script {

    private final String timerID;
    private final int timerDuration;
    private final Script timerExpireScript;

    public ScriptTimerStart(Condition condition, String timerID, int timerDuration, Script timerExpireScript) {
        super(condition);
        this.timerID = timerID;
        this.timerDuration = timerDuration;
        this.timerExpireScript = timerExpireScript;
    }

    @Override
    protected void executeSuccess(ContextScript context) {
        Timer timer = new Timer(context.game(), timerID, timerDuration, timerExpireScript);
        context.game().data().addTimer(timer.getID(), timer);
    }

}
