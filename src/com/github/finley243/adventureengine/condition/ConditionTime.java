package com.github.finley243.adventureengine.condition;

import com.github.finley243.adventureengine.actor.Actor;

public class ConditionTime extends Condition {

    private final int hours1;
    private final int minutes1;
    private final int hours2;
    private final int minutes2;

    public ConditionTime(boolean invert, int hours1, int minutes1, int hours2, int minutes2) {
        super(invert);
        this.hours1 = hours1;
        this.minutes1 = minutes1;
        this.hours2 = hours2;
        this.minutes2 = minutes2;
    }

    @Override
    public boolean isMetInternal(Actor subject, Actor target) {
        return subject.game().data().time().isInRange(hours1, minutes1, hours2, minutes2);
    }

}
