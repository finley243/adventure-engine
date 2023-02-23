package com.github.finley243.adventureengine.actor.ai.behavior;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionSleep;
import com.github.finley243.adventureengine.action.ActionObjectUseStart;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ai.Idle;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.world.environment.Area;

import java.util.List;

public class BehaviorSleep extends Behavior {

    private final String bed;

    public BehaviorSleep(Condition condition, List<Idle> idles, String bed) {
        super(condition, 0, idles);
        this.bed = bed;
    }

    @Override
    public boolean isInTargetState(Actor subject) {
        return subject.isSleeping();
    }

    @Override
    public Area getTargetArea(Actor subject) {
        return subject.game().data().getObject(bed).getArea();
    }

    @Override
    public float actionUtilityOverride(Actor subject, Action action) {
        if(action instanceof ActionObjectUseStart && ((ActionObjectUseStart) action).getComponent().getObject().getID().equals(bed)) {
            return subject.isInCombat() ? BEHAVIOR_ACTION_UTILITY_COMBAT : BEHAVIOR_ACTION_UTILITY;
        } else if(action instanceof ActionSleep) {
            return subject.isInCombat() ? BEHAVIOR_ACTION_UTILITY_COMBAT : BEHAVIOR_ACTION_UTILITY;
        }
        return -1.0f;
    }

}
