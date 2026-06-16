package com.github.finley243.adventureengine.actor.ai;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ai.behavior.Behavior;
import com.github.finley243.adventureengine.script.ScriptRuntime;
import com.github.finley243.adventureengine.world.environment.Area;

import java.util.List;

public class BehaviorComponent {

    private final Actor actor;
    private final Context defaultContext;
    private int currentIndex;
    private AreaTarget areaTarget;
    private Context scriptContext;

    public BehaviorComponent(Actor actor) {
        this.actor = actor;
        this.currentIndex = -1;
        this.defaultContext = Context.builder().subject(actor).target(actor).build();
        resetContext();
    }

    private Behavior currentBehavior() {
        List<Behavior> behaviors = actor.getBehaviors();
        if (behaviors == null || behaviors.isEmpty() || currentIndex == -1 || currentIndex >= behaviors.size()) return null;
        return behaviors.get(currentIndex);
    }

    public Idle getIdle(ScriptRuntime scriptRuntime) {
        Behavior current = currentBehavior();
        if (current == null) {
            return null;
        } else {
            return current.getIdle(actor, scriptRuntime);
        }
    }

    public Float actionUtilityOverride(Action action) {
        Behavior current = currentBehavior();
        if (current == null) {
            return null;
        } else {
            return current.actionUtilityOverride(actor, action);
        }
    }

    public void onPerformAction(Action action) {
        Behavior currentBehavior = currentBehavior();
        if (currentBehavior != null) {
            currentBehavior.onPerformAction(actor, action);
        }
    }

    public void updateTurn(ScriptRuntime scriptRuntime) {
        if (actor.getBehaviors().isEmpty()) return;
        Behavior currentBehavior = currentBehavior();
        if (currentBehavior != null) {
            currentBehavior.updateTurn(actor, scriptRuntime);
        }
    }

    public void update(ScriptRuntime scriptRuntime, Pathfinder pathfinder) {
        if (actor.getBehaviors().isEmpty()) return;
        Behavior currentBehavior = currentBehavior();
        if (currentBehavior != null) {
            currentBehavior.update(actor, scriptRuntime, scriptContext);
        }
        endCurrentBehaviorIfInvalid(scriptRuntime);
        selectNextBehavior(scriptRuntime);
        updateAreaTarget(pathfinder);
    }

    private void endCurrentBehaviorIfInvalid(ScriptRuntime scriptRuntime) {
        Behavior currentBehavior = currentBehavior();
        if (currentBehavior != null && !currentBehavior.isValid(actor, scriptRuntime)) {
            currentIndex = -1;
            areaTarget.markForRemoval();
            areaTarget = null;
        }
    }

    private void updateAreaTarget(Pathfinder pathfinder) {
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
                areaTarget = new AreaTarget(targetArea, targetUtility, true, pathfinder);
                actor.addPursueTarget(areaTarget);
            } else {
                areaTarget.setTargetArea(targetArea);
                areaTarget.setTargetUtility(targetUtility);
            }
        }
    }

    private void selectNextBehavior(ScriptRuntime scriptRuntime) {
        List<Behavior> behaviors = actor.getBehaviors();
        Behavior currentBehavior = currentBehavior();
        boolean onlyHigherPriorities = currentBehavior != null && !currentBehavior.hasCompleted(actor);
        for (int i = 0; i < (onlyHigherPriorities ? currentIndex : behaviors.size()); i++) {
            if (behaviors.get(i).isValid(actor, scriptRuntime)) {
                currentIndex = i;
                resetContext();
                behaviors.get(i).onStart(actor, scriptRuntime, );
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

    private void resetContext() {
        scriptContext = Context.from(defaultContext).build();
    }

}
