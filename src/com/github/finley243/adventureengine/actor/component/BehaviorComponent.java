package com.github.finley243.adventureengine.actor.component;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ai.AreaTarget;
import com.github.finley243.adventureengine.actor.ai.behavior.Behavior;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BehaviorComponent {

    private final Actor actor;
    private final List<Behavior> behaviors;
    private int currentIndex;
    private AreaTarget areaTarget;

    public BehaviorComponent(Actor actor, List<Behavior> behaviors) {
        this.actor = actor;
        if(behaviors == null) {
            this.behaviors = new ArrayList<>();
        } else {
            this.behaviors = behaviors;
        }
        this.currentIndex = -1;
    }

    private Behavior currentBehavior() {
        if(behaviors.isEmpty() || currentIndex == -1 || currentIndex >= behaviors.size()) return null;
        return behaviors.get(currentIndex);
    }

    // A return value of -1.0f indicates no override for given action
    public float actionUtilityOverride(Action action) {
        Behavior current = currentBehavior();
        if(current == null) {
            return -1.0f;
        } else {
            return current.actionUtilityOverride(action);
        }
    }

    public void update() {
        if(behaviors.isEmpty()) return;
        if(currentBehavior() != null && currentBehavior().getTargetArea(actor) != null) {
            if(areaTarget == null) {
                areaTarget = new AreaTarget(Set.of(currentBehavior().getTargetArea(actor)), Behavior.BEHAVIOR_ACTION_UTILITY, false, false, false);
                actor.addPursueTarget(areaTarget);
            } else {
                areaTarget.setTargetAreas(Set.of(currentBehavior().getTargetArea(actor)));
            }
        }
        if(currentBehavior() != null) {
            currentBehavior().update(actor);
        }
        boolean shouldEnd = currentBehavior() != null && currentBehavior().shouldEnd(actor);
        if(shouldEnd) {
            currentIndex = -1;
        }
        if(currentBehavior() == null || !currentBehavior().requireCompleting() || currentBehavior().hasCompleted(actor)) {
            for (int i = 0; i < (shouldEnd ? behaviors.size() : currentIndex); i++) {
                if(behaviors.get(i).shouldStart(actor)) {
                    currentIndex = i;
                    behaviors.get(i).onStart();
                    areaTarget.markForRemoval();
                    break;
                }
            }
        }
    }

}
