package com.github.finley243.adventureengine.actor.component;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ai.AreaTarget;
import com.github.finley243.adventureengine.actor.ai.Idle;
import com.github.finley243.adventureengine.actor.ai.behavior.Behavior;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.object.WorldObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BehaviorComponent {

    private final Actor actor;
    private final List<Behavior> behaviors;
    private int currentIndex;
    private AreaTarget areaTarget;

    public BehaviorComponent(Actor actor, List<Behavior> behaviors) {
        this.actor = actor;
        this.behaviors = Objects.requireNonNullElseGet(behaviors, ArrayList::new);
        this.currentIndex = -1;
    }

    private Behavior currentBehavior() {
        if (behaviors.isEmpty() || currentIndex == -1 || currentIndex >= behaviors.size()) return null;
        return behaviors.get(currentIndex);
    }

    public Idle getIdle() {
        Behavior current = currentBehavior();
        if (current == null) {
            return null;
        } else {
            return current.getIdle(actor);
        }
    }

    // A return value of -1.0f indicates no override for given action
    public Float actionUtilityOverride(Action action) {
        Behavior current = currentBehavior();
        if (current == null) {
            return null;
        } else {
            return current.actionUtilityOverride(actor, action);
        }
    }

    public boolean isGuarding(WorldObject object) {
        Behavior currentBehavior = currentBehavior();
        return currentBehavior != null && currentBehavior.isGuarding(actor, object);
    }

    public void onPerformAction(Action action) {
        Behavior currentBehavior = currentBehavior();
        if (currentBehavior != null) {
            currentBehavior.onPerformAction(action);
        }
    }

    public void updateTurn() {
        if (behaviors.isEmpty()) return;
        Behavior currentBehavior = currentBehavior();
        if (currentBehavior != null) {
            currentBehavior.updateTurn(actor);
        }
    }

    public void update() {
        if (behaviors.isEmpty()) return;
        Behavior currentBehavior = currentBehavior();
        if (currentBehavior != null) {
            currentBehavior.update(actor);
        }
        endCurrentBehaviorIfInvalid();
        //updateAreaTarget();
        selectNextBehavior();
        updateAreaTarget();
    }

    private void endCurrentBehaviorIfInvalid() {
        Behavior currentBehavior = currentBehavior();
        if (currentBehavior != null && !currentBehavior.isValid(actor)) {
            currentIndex = -1;
            areaTarget.markForRemoval();
            areaTarget = null;
        }
    }

    private void updateAreaTarget() {
        Behavior currentBehavior = currentBehavior();
        if (currentBehavior == null) {
            return;
        }
        Area targetArea = currentBehavior.getTargetArea(actor);
        if (targetArea == null) {
            if (areaTarget != null) {
                areaTarget.markForRemoval();
                areaTarget = null;
            }
        } else {
            float targetUtility = actor.isInCombat() ? Behavior.BEHAVIOR_MOVEMENT_UTILITY_COMBAT : Behavior.BEHAVIOR_MOVEMENT_UTILITY;
            if (areaTarget == null) {
                areaTarget = new AreaTarget(targetArea, targetUtility, true);
                actor.addPursueTarget(areaTarget);
            } else {
                areaTarget.setTargetArea(targetArea);
                areaTarget.setTargetUtility(targetUtility);
            }
        }
    }

    private void selectNextBehavior() {
        Behavior currentBehavior = currentBehavior();
        boolean onlyHigherPriorities = currentBehavior != null && !currentBehavior.hasCompleted(actor);
        for (int i = 0; i < (onlyHigherPriorities ? currentIndex : behaviors.size()); i++) {
            if (behaviors.get(i).isValid(actor)) {
                currentIndex = i;
                behaviors.get(i).onStart();
                if (areaTarget != null) {
                    if (behaviors.get(i).getTargetArea(actor) != null) {
                        areaTarget.setTargetArea(behaviors.get(i).getTargetArea(actor));
                    } else {
                        areaTarget.markForRemoval();
                        areaTarget = null;
                    }
                }
                return;
            }
        }
    }

}
