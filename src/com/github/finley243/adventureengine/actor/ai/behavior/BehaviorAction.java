package com.github.finley243.adventureengine.actor.ai.behavior;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ai.Idle;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.world.environment.Area;

import java.util.List;

public class BehaviorAction extends Behavior {

    private final String actionID;
    private final Condition actionCondition;

    private boolean hasPerformedAction;

    public BehaviorAction(Condition condition, Script eachRoundScript, int duration, List<Idle> idles, String actionID, Condition actionCondition) {
        super(condition, eachRoundScript, duration, idles);
        this.actionID = actionID;
        this.actionCondition = actionCondition;
        this.hasPerformedAction = false;
    }

    @Override
    public void onStart() {
        hasPerformedAction = false;
        super.onStart();
    }

    @Override
    public boolean isInTargetState(Actor subject) {
        return hasPerformedAction;
    }

    @Override
    public Area getTargetArea(Actor subject) {
        return null;
    }

    @Override
    public void onPerformAction(Actor subject, Action action) {
        if (actionIsMatch(subject, action)) {
            hasPerformedAction = true;
        }
        super.onPerformAction(subject, action);
    }

    @Override
    public Float actionUtilityOverride(Actor subject, Action action) {
        if (actionIsMatch(subject, action)) {
            return subject.isInCombat() ? BEHAVIOR_ACTION_UTILITY_COMBAT : BEHAVIOR_ACTION_UTILITY;
        }
        return super.actionUtilityOverride(subject, action);
    }

    private boolean actionIsMatch(Actor subject, Action action) {
        if (!action.getID().equals(actionID)) {
            return false;
        }
        return actionCondition == null || actionCondition.isMet(action.getContext(subject));
    }

}
