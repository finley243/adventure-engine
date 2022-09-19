package com.github.finley243.adventureengine.condition;

import com.github.finley243.adventureengine.actor.Actor;

public class ConditionTimerActive extends Condition {

    private final String timerID;

    public ConditionTimerActive(boolean invert, String timerID) {
        super(invert);
        this.timerID = timerID;
    }

    @Override
    protected boolean isMetInternal(Actor subject, Actor target) {
        return subject.game().data().isTimerActive(timerID);
    }

}
