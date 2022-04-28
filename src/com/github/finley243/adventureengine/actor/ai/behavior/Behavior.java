package com.github.finley243.adventureengine.actor.ai.behavior;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.scene.SceneManager;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.object.WorldObject;

import java.util.List;

public abstract class Behavior {

    public static final float BEHAVIOR_ACTION_UTILITY = 1.0f;

    private final Condition condition;
    // If duration = 0, behavior will continue indefinitely until endCondition is met or until superseded by another behavior
    private final int duration;
    private final List<Scene> idleScenes;
    //private final boolean allowCombatActions;
    //private final boolean allowItemActions;
    private int turnsRemaining;

    public Behavior(Condition condition, int duration, List<Scene> idleScenes) {
        this.condition = condition;
        this.duration = duration;
        this.idleScenes = idleScenes;
        this.turnsRemaining = 0;
    }

    // Whether the turnsRemaining counter should be counted down
    public abstract boolean isInTargetState(Actor subject);

    public void update(Actor subject) {
        if(duration > 0 && turnsRemaining > 0) {
            if(isInTargetState(subject)) {
                turnsRemaining -= 1;
            } else {
                // Reset counter if countdown condition is interrupted
                turnsRemaining = duration;
            }
        }
        // TODO - Stop calling SceneManager directly from Behavior (could call from BehaviorComponent? from idle action?)
        if(isInTargetState(subject) && idleScenes != null) {
            SceneManager.trigger(subject.game(), idleScenes);
        }
        //System.out.println("Turns Remaining: " + turnsRemaining);
    }

    public void onStart() {
        turnsRemaining = duration;
    }

    public abstract Area getTargetArea(Actor subject);

    public boolean hasCompleted(Actor subject) {
        if(duration > 0) {
            return turnsRemaining == 0;
        } else {
            return isInTargetState(subject);
        }
    }

    public abstract float actionUtilityOverride(Action action);

    public boolean isGuarding(Actor subject, WorldObject object) {
        return false;
    }

    public boolean isValid(Actor subject) {
        return condition == null || condition.isMet(subject);
    }

}
