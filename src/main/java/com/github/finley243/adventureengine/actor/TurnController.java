package com.github.finley243.adventureengine.actor;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.ai.Pathfinder;
import com.github.finley243.adventureengine.actor.ai.UtilityUtils;
import com.github.finley243.adventureengine.event.SensoryEventDispatcher;
import com.github.finley243.adventureengine.menu.MenuManager;
import com.github.finley243.adventureengine.quest.QuestManager;
import com.github.finley243.adventureengine.script.ScriptRuntime;

import java.util.List;

public class TurnController {

    private final SensoryEventDispatcher sensoryEventDispatcher;

    public TurnController(SensoryEventDispatcher sensoryEventDispatcher) {
        this.sensoryEventDispatcher = sensoryEventDispatcher;
    }

    public void takeTurn(Actor actor, Pathfinder pathfinder, ScriptRuntime scriptRuntime, MenuManager menuManager, QuestManager questManager, Game game) {
        if (!actor.isEnabled() || actor.isDead()) {
            game.onEndTurn(actor);
            return;
        }
        if (actor.isSleeping()) {
            actor.updateSleep();
            game.onEndTurn(actor);
            return;
        }
        actor.getEffectComponent().onStartRound();
        actor.getInventory().onStartRound();
        if (!actor.isPlayerControlled()) {
            actor.getTargetingComponent().updateTurn();
            actor.getBehaviorComponent().updateTurn(scriptRuntime);
        }
        actor.setActionPointsUsed(0);
        actor.clearRepeatActions();
        actor.setTurnActive();
        Action lastAction = null;
        int repeatActionCount = 0;
        while (!actor.isTurnEnded()) {
            questManager.update();
            if (!actor.isPlayerControlled()) {
                actor.updateAreaTargets();
                actor.getTargetingComponent().update();
                actor.getBehaviorComponent().update(scriptRuntime, pathfinder);
            }
            List<Action> actionChoices = actor.availableActions(pathfinder);
            if (actionChoices.isEmpty()) {
                actor.endTurn();
                break;
            }
            Action selectedAction;
            if (actor.isPlayerControlled()) {
                selectedAction = menuManager.actionChoiceMenu(actor, actionChoices);
            } else {
                selectedAction = chooseAIAction(actor, actionChoices);
            }
            boolean isRepeatMatch = false;
            Action actionRepeatMatch = actor.getRepeatActionMatch(selectedAction);
            if (actionRepeatMatch != null) {
                isRepeatMatch = true;
                actor.decrementRepeatAction(actionRepeatMatch);
            }
            if (!(isRepeatMatch && selectedAction.repeatsUseNoActionPoints())) {
                actor.setActionPointsUsed(actor.getActionPointsUsed() + selectedAction.actionPoints(actor));
            }
            if (!isRepeatMatch && selectedAction.repeatCount(actor) > 0) {
                actor.addRepeatAction(selectedAction, selectedAction.repeatCount(actor) - 1);
            }
            if (lastAction != null && selectedAction.isRepeatMatch(lastAction)) {
                repeatActionCount += 1;
            } else {
                repeatActionCount = 0;
            }
            selectedAction.choose(actor, repeatActionCount, sensoryEventDispatcher);
            actor.getBehaviorComponent().onPerformAction(selectedAction);
            lastAction = selectedAction;
        }
        game.onEndTurn(actor);
    }

    public Action chooseAIAction(Actor actor, List<Action> actions) {
        return UtilityUtils.selectActionByUtility(actor, actions, 1);
    }

}
