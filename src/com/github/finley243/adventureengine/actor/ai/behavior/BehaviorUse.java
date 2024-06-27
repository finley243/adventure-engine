package com.github.finley243.adventureengine.actor.ai.behavior;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionObjectUseEnd;
import com.github.finley243.adventureengine.action.ActionObjectUseStart;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ai.Idle;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.world.environment.Area;

import java.util.List;

public class BehaviorUse extends Behavior {

    private final String object;
    private final String slot;

    public BehaviorUse(Condition condition, Script eachRoundScript, int duration, List<Idle> idles, String object, String slot) {
        super(condition, eachRoundScript, duration, idles);
        this.object = object;
        this.slot = slot;
    }

    @Override
    public boolean isInTargetState(Actor subject) {
        return subject.isUsingObject() && subject.getUsingObject().object().getID().equals(object) && subject.getUsingObject().slot().equals(slot);
    }

    @Override
    public Area getTargetArea(Actor subject) {
        return subject.game().data().getObject(object).getArea();
    }

    @Override
    public Float actionUtilityOverride(Actor subject, Action action) {
        if (action instanceof ActionObjectUseStart actionUseStart && actionUseStart.getComponent().getObject().getID().equals(object) && actionUseStart.getSlotID().equals(slot)) {
            return subject.isInCombat() ? BEHAVIOR_ACTION_UTILITY_COMBAT : BEHAVIOR_ACTION_UTILITY;
        } else if (action instanceof ActionObjectUseEnd actionUseEnd && actionUseEnd.getComponent().getObject().getID().equals(object) && actionUseEnd.getSlotID().equals(slot)) {
            return 0.0f;
        }
        return super.actionUtilityOverride(subject, action);
    }

}
