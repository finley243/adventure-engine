package com.github.finley243.adventureengine.condition;

import com.github.finley243.adventureengine.Context;

public class ConditionTimerActive extends Condition {

    private final String timerID;

    public ConditionTimerActive(boolean invert, String timerID) {
        super(invert);
        this.timerID = timerID;
    }

    @Override
    protected boolean isMetInternal(Context context) {
        return context.game().data().isTimerActive(timerID);
    }

}
