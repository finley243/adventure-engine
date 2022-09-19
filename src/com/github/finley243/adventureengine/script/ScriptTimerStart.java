package com.github.finley243.adventureengine.script;

import com.github.finley243.adventureengine.Timer;
import com.github.finley243.adventureengine.actor.Actor;
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
    protected void executeSuccess(Actor subject, Actor target) {
        Timer timer = new Timer(subject.game(), timerID, timerDuration, timerExpireScript);
        subject.game().data().addTimer(timer.getID(), timer);
    }

}
