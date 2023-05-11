package com.github.finley243.adventureengine.actor.ai.behavior;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionObjectUseEnd;
import com.github.finley243.adventureengine.action.ActionObjectUseStart;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ai.Idle;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.world.environment.Area;

import java.util.List;

public class BehaviorUse extends Behavior {

    private final String object;
    private final String component;

    public BehaviorUse(Condition condition, int duration, List<Idle> idles, String object, String component) {
        super(condition, duration, idles);
        this.object = object;
        this.component = component;
    }

    @Override
    public boolean isInTargetState(Actor subject) {
        return subject.isUsingObject() && subject.getUsingObject().getObject().getID().equals(object) && subject.getUsingObject().getID().equals(component);
    }

    @Override
    public Area getTargetArea(Actor subject) {
        return subject.game().data().getObject(object).getArea();
    }

    @Override
    public float actionUtilityOverride(Actor subject, Action action) {
        if (action instanceof ActionObjectUseStart actionUseStart && actionUseStart.getComponent().getObject().getID().equals(object) && actionUseStart.getComponent().getID().equals(component)) {
            return subject.isInCombat() ? BEHAVIOR_ACTION_UTILITY_COMBAT : BEHAVIOR_ACTION_UTILITY;
        } else if(action instanceof ActionObjectUseEnd actionUseEnd && actionUseEnd.getComponent().getObject().getID().equals(object) && actionUseEnd.getComponent().getID().equals(component)) {
            return 0.0f;
        }
        return -1.0f;
    }

}
