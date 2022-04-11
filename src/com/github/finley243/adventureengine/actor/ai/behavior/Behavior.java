package com.github.finley243.adventureengine.actor.ai.behavior;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.scene.SceneManager;
import com.github.finley243.adventureengine.world.environment.Area;

import java.util.List;

public abstract class Behavior {

    public static final float BEHAVIOR_ACTION_UTILITY = 1.0f;

    private final Condition startCondition;
    private final Condition endCondition;
    // If duration = 0, behavior will continue indefinitely until endCondition is met or until superseded by another behavior
    private final int duration;
    private final boolean requireCompleting;
    private final List<String> idleScenes;
    //private final boolean allowCombatActions;
    //private final boolean allowItemActions;
    private int turnsRemaining;

    public Behavior(Condition startCondition, Condition endCondition, int duration, boolean requireCompleting, List<String> idleScenes) {
        this.startCondition = startCondition;
        this.endCondition = endCondition;
        this.duration = duration;
        this.requireCompleting = requireCompleting;
        this.idleScenes = idleScenes;
    }

    // Whether the turnsRemaining counter should be counted down
    public abstract boolean isInTargetState(Actor subject);

    public void update(Actor subject) {
        if(duration > 0) {
            if(isInTargetState(subject)) {
                turnsRemaining -= 1;
            } else {
                // Reset counter if countdown condition is interrupted
                turnsRemaining = duration;
            }
        }
        // May want to change how idle scenes are executed later
        if(isInTargetState(subject) && idleScenes != null) {
            SceneManager.trigger(subject.game(), idleScenes);
        }
    }

    public void onStart() {
        turnsRemaining = duration;
    }

    public abstract Area getTargetArea(Actor subject);

    public boolean hasCompleted(Actor subject) {
        if(duration > 0) {
            return turnsRemaining == 0;
        }
        return false;
    }

    public abstract float actionUtilityOverride(Action action);

    public boolean requireCompleting() {
        return requireCompleting;
    }

    public boolean shouldStart(Actor subject) {
        return startCondition == null || startCondition.isMet(subject);
    }

    public boolean shouldEnd(Actor subject) {
        if(requireCompleting && !hasCompleted(subject)) {
            return false;
        } else {
            return hasCompleted(subject) || (duration > 0 && turnsRemaining == 0) || (endCondition != null && endCondition.isMet(subject));
        }
    }

}
