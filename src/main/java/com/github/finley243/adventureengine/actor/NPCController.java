package com.github.finley243.adventureengine.actor;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.ai.Pathfinder;
import com.github.finley243.adventureengine.actor.ai.UtilityUtils;
import com.github.finley243.adventureengine.event.SensoryEventDispatcher;
import com.github.finley243.adventureengine.menu.MenuManager;
import com.github.finley243.adventureengine.script.ScriptRuntime;

import java.util.List;

public class NPCController extends TurnController {

    public NPCController(Actor actor, SensoryEventDispatcher sensoryEventDispatcher, MenuManager menuManager) {
        super(actor, sensoryEventDispatcher, menuManager);
    }

    @Override
    protected void onPreAction(ScriptRuntime scriptRuntime, Pathfinder pathfinder) {
        if (!actor.isPlayerControlled()) {
            actor.updateAreaTargets();
            actor.getTargetingComponent().update();
            actor.getBehaviorComponent().update(scriptRuntime, pathfinder);
        }
    }

    @Override
    protected void onPostAction(Action action) {
        actor.getBehaviorComponent().onPerformAction(action);
    }

    @Override
    protected void onStartTurn(ScriptRuntime scriptRuntime) {
        if (!actor.isPlayerControlled()) {
            actor.getTargetingComponent().updateTurn();
            actor.getBehaviorComponent().updateTurn(scriptRuntime);
        }
    }

    @Override
    protected Action selectAction(List<Action> actions) {
        if (actor.isPlayerControlled()) {
            return menuManager.actionChoiceMenu(actor, actions);
        } else {
            return UtilityUtils.selectActionByUtility(actor, actions, 1);
        }
    }

}
