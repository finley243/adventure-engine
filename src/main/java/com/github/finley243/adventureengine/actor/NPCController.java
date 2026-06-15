package com.github.finley243.adventureengine.actor;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.ai.Idle;
import com.github.finley243.adventureengine.actor.ai.Pathfinder;
import com.github.finley243.adventureengine.actor.ai.UtilityUtils;
import com.github.finley243.adventureengine.actor.component.BehaviorComponent;
import com.github.finley243.adventureengine.event.SensoryEventDispatcher;
import com.github.finley243.adventureengine.menu.MenuManager;
import com.github.finley243.adventureengine.script.ScriptRuntime;

import java.util.List;

public class NPCController extends TurnController {

    private final BehaviorComponent behaviorComponent;

    public NPCController(Actor actor, SensoryEventDispatcher sensoryEventDispatcher, MenuManager menuManager) {
        super(actor, sensoryEventDispatcher, menuManager);
        this.behaviorComponent = new BehaviorComponent(actor);
    }

    @Override
    protected void onStartTurn(ScriptRuntime scriptRuntime) {
        if (!actor.isPlayerControlled()) {
            actor.getTargetingComponent().updateTurn();
            behaviorComponent.updateTurn(scriptRuntime);
        }
    }

    @Override
    protected void onPreAction(ScriptRuntime scriptRuntime, Pathfinder pathfinder) {
        if (!actor.isPlayerControlled()) {
            actor.updateAreaTargets();
            actor.getTargetingComponent().update();
            behaviorComponent.update(scriptRuntime, pathfinder);
        }
    }

    @Override
    protected void onPostAction(Action action) {
        if (!actor.isPlayerControlled()) {
            behaviorComponent.onPerformAction(action);
        }
    }

    @Override
    protected void onEndTurnAction(ScriptRuntime scriptRuntime) {
        if (!actor.isPlayerControlled() && canIdle()) {
            Idle idle = behaviorComponent.getIdle(scriptRuntime);
            if (idle != null) {
                idle.trigger(actor);
            }
        }
    }

    @Override
    protected Action selectAction(List<Action> actions) {
        if (actor.isPlayerControlled()) {
            return menuManager.actionChoiceMenu(actor, this, actions);
        } else {
            return UtilityUtils.selectActionByUtility(actor, behaviorComponent, actions, 1);
        }
    }

    private boolean canIdle() {
        return !actor.isInCombat();
    }

}
