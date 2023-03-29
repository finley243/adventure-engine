package com.github.finley243.adventureengine.actor.ai.behavior;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ai.Idle;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.world.environment.Area;

import java.util.List;

public class BehaviorVendor extends Behavior {

    private final String area;

    public BehaviorVendor(Condition condition, int duration, List<Idle> idles, String area) {
        super(condition, duration, idles);
        this.area = area;
    }

    @Override
    public boolean isInTargetState(Actor subject) {
        return subject.getArea().getID().equals(area);
    }

    @Override
    public Area getTargetArea(Actor subject) {
        return subject.game().data().getArea(area);
    }

    @Override
    public boolean isVendingEnabled(Actor subject) {
        return subject.getArea().getID().equals(area);
    }
}
