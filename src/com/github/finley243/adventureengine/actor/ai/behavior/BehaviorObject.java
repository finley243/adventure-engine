package com.github.finley243.adventureengine.actor.ai.behavior;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionUseStart;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ai.Idle;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.world.environment.Area;

import java.util.List;

public class BehaviorObject extends Behavior {

    private final String object;

    public BehaviorObject(Condition condition, int duration, List<Idle> idles, String object) {
        super(condition, duration, idles);
        this.object = object;
    }

    @Override
    public boolean isInTargetState(Actor subject) {
        return subject.isUsingObject() && subject.getUsingObject().getID().equals(object);
    }

    @Override
    public Area getTargetArea(Actor subject) {
        return subject.game().data().getObject(object).getArea();
    }

    @Override
    public float actionUtilityOverride(Actor subject, Action action) {
        if(action instanceof ActionUseStart && ((ActionUseStart) action).getObject().getID().equals(object)) {
            return subject.isInCombat() ? BEHAVIOR_ACTION_UTILITY_COMBAT : BEHAVIOR_ACTION_UTILITY;
        }/* else if(action instanceof ActionUseStop && ((ActionUseStop) action).getObject().getID().equals(object)) {
            return 0.0f;
        }*/
        return -1.0f;
    }

}
