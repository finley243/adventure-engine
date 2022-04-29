package com.github.finley243.adventureengine.actor.component;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ai.AreaTarget;
import com.github.finley243.adventureengine.actor.ai.behavior.Behavior;
import com.github.finley243.adventureengine.world.object.WorldObject;

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

    public boolean isGuarding(WorldObject object) {
        Behavior currentBehavior = currentBehavior();
        return currentBehavior != null && currentBehavior.isGuarding(actor, object);
    }

    public boolean isVendingEnabled() {
        Behavior currentBehavior = currentBehavior();
        return currentBehavior != null && currentBehavior.isVendingEnabled(actor);
    }

    public void update() {
        if(behaviors.isEmpty()) return;
        Behavior currentBehavior = currentBehavior();
        if(currentBehavior != null) {
            if(areaTarget == null) {
                if(currentBehavior.getTargetArea(actor) != null) {
                    areaTarget = new AreaTarget(currentBehavior.getTargetArea(actor), Behavior.BEHAVIOR_ACTION_UTILITY, true, false, false);
                    actor.addPursueTarget(areaTarget);
                }
            } else {
                if(currentBehavior.getTargetArea(actor) != null) {
                    areaTarget.setTargetArea(currentBehavior.getTargetArea(actor));
                } else {
                    areaTarget.markForRemoval();
                    areaTarget = null;
                }
            }
            currentBehavior.update(actor);
        }
        if(currentBehavior != null && !currentBehavior.isValid(actor)) {
            currentBehavior = null;
            currentIndex = -1;
            areaTarget.markForRemoval();
            areaTarget = null;
        }
        if(currentBehavior == null || currentBehavior.hasCompleted(actor)) {
            for (int i = 0; i < (currentBehavior == null ? behaviors.size() : currentIndex); i++) {
                if(behaviors.get(i).isValid(actor)) {
                    currentIndex = i;
                    behaviors.get(i).onStart();
                    if(areaTarget != null) {
                        if(behaviors.get(i).getTargetArea(actor) != null) {
                            areaTarget.setTargetArea(behaviors.get(i).getTargetArea(actor));
                        } else {
                            areaTarget.markForRemoval();
                            areaTarget = null;
                        }
                    }
                    break;
                }
            }
        }
    }

}
