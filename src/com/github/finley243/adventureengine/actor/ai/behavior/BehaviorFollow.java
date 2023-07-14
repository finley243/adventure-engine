package com.github.finley243.adventureengine.actor.ai.behavior;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ai.Idle;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.world.environment.Area;

import java.util.List;

public class BehaviorFollow extends Behavior {

    private final String actor;

    public BehaviorFollow(Condition condition, Script eachRoundScript, int duration, List<Idle> idles, String actor) {
        super(condition, eachRoundScript, duration, idles);
        this.actor = actor;
    }

    @Override
    public boolean isInTargetState(Actor subject) {
        return subject.game().data().getActor(actor).getArea().equals(subject.getArea());
    }

    @Override
    public Area getTargetArea(Actor subject) {
        return subject.game().data().getActor(actor).getArea();
    }
}
