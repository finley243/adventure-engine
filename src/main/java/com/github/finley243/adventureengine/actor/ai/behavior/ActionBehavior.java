package com.github.finley243.adventureengine.actor.ai.behavior;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ai.Idle;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.gamedata.Registry;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.script.ScriptRuntime;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.object.WorldObject;

import java.util.List;

public class ActionBehavior extends Behavior {

    private final String actionID;
    private final Condition actionCondition;

    private boolean hasPerformedAction;

    public ActionBehavior(Condition condition, Script startScript, Script eachRoundScript, int duration, List<Idle> idles, String actionID, Condition actionCondition) {
        super(condition, startScript, eachRoundScript, duration, idles);
        this.actionID = actionID;
        this.actionCondition = actionCondition;
        this.hasPerformedAction = false;
    }

    @Override
    public void resolveReferences(Registry<Area> areaRegistry, Registry<WorldObject> objectRegistry, Registry<Actor> actorRegistry) {}

    @Override
    public void onStart(Actor actor, ScriptRuntime scriptRuntime, Context scriptContext) {
        hasPerformedAction = false;
        super.onStart(actor, scriptRuntime, scriptContext);
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
