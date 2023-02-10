package com.github.finley243.adventureengine.condition;

import com.github.finley243.adventureengine.ContextScript;

public class ConditionTimerActive extends Condition {

    private final String timerID;

    public ConditionTimerActive(boolean invert, String timerID) {
        super(invert);
        this.timerID = timerID;
    }

    @Override
    protected boolean isMetInternal(ContextScript context) {
        return context.game().data().isTimerActive(timerID);
    }

}
