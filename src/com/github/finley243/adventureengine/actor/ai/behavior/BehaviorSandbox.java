package com.github.finley243.adventureengine.actor.ai.behavior;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionUseStart;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.object.ObjectBed;

import java.util.List;

public class BehaviorSandbox extends Behavior {

    private final String area;

    public BehaviorSandbox(Condition condition, int duration, List<String> idleScenes, String area) {
        super(condition, duration, idleScenes);
        this.area = area;
    }

    @Override
    public boolean isInTargetState(Actor subject) {
        return subject.getArea().getRoom().equals(subject.game().data().getArea(area).getRoom());
    }

    @Override
    public Area getTargetArea(Actor subject) {
        return subject.game().data().getArea(area);
    }

    @Override
    public float actionUtilityOverride(Action action) {
        if(action instanceof ActionUseStart && !(((ActionUseStart) action).getObject() instanceof ObjectBed)) {
            return BEHAVIOR_ACTION_UTILITY;
        }
        return -1.0f;
    }

}