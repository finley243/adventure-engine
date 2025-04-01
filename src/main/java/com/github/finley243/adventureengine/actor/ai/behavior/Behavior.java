package com.github.finley243.adventureengine.actor.ai.behavior;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.MathUtils;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ai.Idle;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.object.WorldObject;

import java.util.ArrayList;
import java.util.List;

public abstract class Behavior {

    public static final Float BEHAVIOR_ACTION_UTILITY = 0.7f;
    // To avoid conflicts, behaviors are ignored (-1.0f prevents overriding) during combat
    public static final Float BEHAVIOR_ACTION_UTILITY_COMBAT = null;
    public static final float BEHAVIOR_MOVEMENT_UTILITY = 0.7f;
    public static final float BEHAVIOR_MOVEMENT_UTILITY_COMBAT = 0.0f;

    private final Condition condition;
    private final Script startScript;
    private final Script eachRoundScript;
    // If duration = 0, behavior will continue indefinitely until endCondition is met or until superseded by another behavior
    private final int duration;
    private final List<Idle> idles;
    private int turnsRemaining;

    public Behavior(Condition condition, Script startScript, Script eachRoundScript, int duration, List<Idle> idles) {
        this.condition = condition;
        this.startScript = startScript;
        this.eachRoundScript = eachRoundScript;
        this.duration = duration;
        this.idles = idles;
        this.turnsRemaining = 0;
    }

    public void triggerStartScript(Context context) {
        if (startScript != null) {
            startScript.execute(context);
        }
    }

    public void triggerRoundScript(Context context) {
        if (eachRoundScript != null) {
            eachRoundScript.execute(context);
        }
    }

    // Whether the turnsRemaining counter should be counted down
    public abstract boolean isInTargetState(Actor subject);

    public void updateTurn(Actor subject, Context scriptContext) {
        triggerRoundScript(scriptContext);
        if (duration > 0 && turnsRemaining > 0) {
            if (isInTargetState(subject)) {
                turnsRemaining -= 1;
            } else {
                // Reset counter if countdown condition is interrupted
                turnsRemaining = duration;
            }
        }
    }

    public void update(Actor subject, Context scriptContext) {
        if (duration > 0 && turnsRemaining > 0 && !isInTargetState(subject)) {
            // Reset counter if countdown condition is interrupted
            turnsRemaining = duration;
        }
    }

    public void onStart(Context scriptContext) {
        turnsRemaining = duration;
        triggerStartScript(scriptContext);
    }

    public abstract Area getTargetArea(Actor subject);

    public boolean hasCompleted(Actor subject) {
        if (duration > 0) {
            return turnsRemaining == 0;
        } else {
            return isInTargetState(subject);
        }
    }

    public void onPerformAction(Actor subject, Action action) {}

    public Float actionUtilityOverride(Actor subject, Action action) {
        return null;
    }

    public boolean isGuarding(Actor subject, WorldObject object) {
        return false;
    }

    public boolean isValid(Actor subject) {
        return condition == null || condition.isMet(new Context(subject.game(), subject, subject));
    }

    public Idle getIdle(Actor subject) {
        if (idles == null) return null;
        List<Idle> validIdles = new ArrayList<>();
        for (Idle idle : idles) {
            if (idle.canPlay(subject)) {
                validIdles.add(idle);
            }
        }
        return MathUtils.selectRandomFromList(validIdles);
    }

}
