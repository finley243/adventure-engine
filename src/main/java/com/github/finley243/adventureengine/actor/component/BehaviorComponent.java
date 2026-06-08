package com.github.finley243.adventureengine.actor.component;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.Game;
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
    private final Context defaultContext;
    private final List<Behavior> behaviors;
    private int currentIndex;
    private AreaTarget areaTarget;
    private Context scriptContext;

    public BehaviorComponent(Game game, Actor actor, List<Behavior> behaviors) {
        this.actor = actor;
        this.behaviors = Objects.requireNonNullElseGet(behaviors, ArrayList::new);
        this.currentIndex = -1;
        this.defaultContext = Context.builder(game).subject(actor).target(actor).build();
        resetContext();
    }

    private Behavior currentBehavior() {
        if (behaviors.isEmpty() || currentIndex == -1 || currentIndex >= behaviors.size()) return null;
        return behaviors.get(currentIndex);
    }

    public Idle getIdle(Game game) {
        Behavior current = currentBehavior();
        if (current == null) {
            return null;
        } else {
            return current.getIdle(game, actor);
        }
    }

    // A return value of -1.0f indicates no override for given action
    public Float actionUtilityOverride(Game game, Action action) {
        Behavior current = currentBehavior();
        if (current == null) {
            return null;
        } else {
            return current.actionUtilityOverride(game, actor, action);
        }
    }

    public boolean isGuarding(WorldObject object) {
        Behavior currentBehavior = currentBehavior();
        return currentBehavior != null && currentBehavior.isGuarding(actor, object);
    }

    public void onPerformAction(Game game, Action action) {
        Behavior currentBehavior = currentBehavior();
        if (currentBehavior != null) {
            currentBehavior.onPerformAction(game, actor, action);
        }
    }

    public void updateTurn(Game game) {
        if (behaviors.isEmpty()) return;
        Behavior currentBehavior = currentBehavior();
        if (currentBehavior != null) {
            currentBehavior.updateTurn(game, actor, scriptContext);
        }
    }

    public void update(Game game) {
        if (behaviors.isEmpty()) return;
        Behavior currentBehavior = currentBehavior();
        if (currentBehavior != null) {
            currentBehavior.update(game, actor, scriptContext);
        }
        endCurrentBehaviorIfInvalid(game);
        selectNextBehavior(game);
        updateAreaTarget(game);
    }

    private void endCurrentBehaviorIfInvalid(Game game) {
        Behavior currentBehavior = currentBehavior();
        if (currentBehavior != null && !currentBehavior.isValid(game, actor)) {
            currentIndex = -1;
            areaTarget.markForRemoval();
            areaTarget = null;
        }
    }

    private void updateAreaTarget(Game game) {
        Behavior currentBehavior = currentBehavior();
        if (currentBehavior == null) {
            return;
        }
        Area targetArea = currentBehavior.getTargetArea(game, actor);
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

    private void selectNextBehavior(Game game) {
        Behavior currentBehavior = currentBehavior();
        boolean onlyHigherPriorities = currentBehavior != null && !currentBehavior.hasCompleted(game, actor);
        for (int i = 0; i < (onlyHigherPriorities ? currentIndex : behaviors.size()); i++) {
            if (behaviors.get(i).isValid(game, actor)) {
                currentIndex = i;
                resetContext();
                behaviors.get(i).onStart(scriptContext);
                if (areaTarget != null) {
                    if (behaviors.get(i).getTargetArea(game, actor) != null) {
                        areaTarget.setTargetArea(behaviors.get(i).getTargetArea(game, actor));
                    } else {
                        areaTarget.markForRemoval();
                        areaTarget = null;
                    }
                }
                return;
            }
        }
    }

    private void resetContext() {
        scriptContext = Context.from(defaultContext).build();
    }

}
