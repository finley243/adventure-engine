package com.github.finley243.adventureengine.actor.ai.behavior;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.object.WorldObject;

import java.util.List;

public class BehaviorGuard extends Behavior {

    private final String guardTarget;

    public BehaviorGuard(Condition condition, int duration, List<String> idleScenes, String guardTarget) {
        super(condition, duration, idleScenes);
        this.guardTarget = guardTarget;
    }

    @Override
    public boolean isInTargetState(Actor subject) {
        WorldObject guardObject = subject.game().data().getObject(guardTarget);
        return guardObject.getArea().equals(subject.getArea());
    }

    @Override
    public Area getTargetArea(Actor subject) {
        return subject.game().data().getObject(guardTarget).getArea();
    }

    @Override
    public float actionUtilityOverride(Action action) {
        return -1.0f;
    }

    @Override
    public boolean isGuarding(Actor subject, WorldObject object) {
        return object.getArea().equals(subject.getArea()) && object.getID().equals(guardTarget);
    }

}
