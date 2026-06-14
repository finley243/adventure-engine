package com.github.finley243.adventureengine.actor;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.ai.Pathfinder;
import com.github.finley243.adventureengine.actor.ai.UtilityUtils;
import com.github.finley243.adventureengine.event.SensoryEventDispatcher;
import com.github.finley243.adventureengine.menu.MenuManager;
import com.github.finley243.adventureengine.quest.QuestManager;
import com.github.finley243.adventureengine.script.ScriptRuntime;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class TurnController {

    private final Actor actor;
    private final SensoryEventDispatcher sensoryEventDispatcher;

    private final Map<Action, Integer> repeatActions;
    private int actionPointsUsed;

    public TurnController(Actor actor, SensoryEventDispatcher sensoryEventDispatcher) {
        this.actor = actor;
        this.sensoryEventDispatcher = sensoryEventDispatcher;
        this.repeatActions = new HashMap<>();
    }

    public void takeTurn(Pathfinder pathfinder, ScriptRuntime scriptRuntime, MenuManager menuManager, QuestManager questManager) {
        if (!actor.isEnabled() || actor.isDead()) {
            return;
        }
        if (actor.isSleeping()) {
            actor.updateSleep();
            return;
        }
        actor.getEffectComponent().onStartRound();
        actor.getInventory().onStartRound();
        if (!actor.isPlayerControlled()) {
            actor.getTargetingComponent().updateTurn();
            actor.getBehaviorComponent().updateTurn(scriptRuntime);
        }
        actionPointsUsed = 0;
        repeatActions.clear();
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
            applyActionConstraints(actionChoices);
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
            Action actionRepeatMatch = getRepeatActionMatch(selectedAction);
            if (actionRepeatMatch != null) {
                isRepeatMatch = true;
                decrementRepeatAction(actionRepeatMatch);
            }
            if (!(isRepeatMatch && selectedAction.repeatsUseNoActionPoints())) {
                actionPointsUsed += selectedAction.actionPoints(actor);
            }
            if (!isRepeatMatch && selectedAction.repeatCount(actor) > 0) {
                addRepeatAction(selectedAction, selectedAction.repeatCount(actor) - 1);
            }
            if (lastAction != null && selectedAction.isRepeatMatch(lastAction)) {
                repeatActionCount += 1;
            } else {
                repeatActionCount = 0;
            }
            selectedAction.choose(actor, repeatActionCount);
            onPostAction(selectedAction);
            lastAction = selectedAction;
        }
    }

    protected Actor getActor() {
        return actor;
    }

    protected abstract void onPostAction(Action action);

    private Action chooseAIAction(Actor actor, List<Action> actions) {
        return UtilityUtils.selectActionByUtility(actor, actions, 1);
    }

    private void applyActionConstraints(List<Action> actions) {
        actions.removeIf(action -> !action.canShow(actor));
        for (Action currentAction : actions) {
            boolean isRepeatMatch = false;
            boolean isBlocked = false;
            boolean isRepeatBlocked = false;
            for (Action blockedAction : repeatActions.keySet()) {
                if (blockedAction.isRepeatMatch(currentAction)) {
                    isRepeatMatch = true;
                }
                if (repeatActions.get(blockedAction) <= 0) {
                    if (isRepeatMatch) {
                        isRepeatBlocked = true;
                        break;
                    } else if (blockedAction.isBlockedMatch(currentAction)) {
                        isBlocked = true;
                        break;
                    }
                }
            }
            if (isRepeatBlocked) {
                currentAction.setDisabled(true, "Repeat limit reached");
            } else if (isBlocked) {
                currentAction.setDisabled(true, "Blocked");
            } else if (!(isRepeatMatch && currentAction.repeatsUseNoActionPoints()) && actor.getActionPoints() - actionPointsUsed < currentAction.actionPoints(actor)) {
                currentAction.setDisabled(true, "Not enough action points");
            }
        }
    }

    private int getRepeatActionCount(Action action) {
        if (!repeatActions.containsKey(action)) throw new IllegalArgumentException("Action is not present in repeatActions");
        return repeatActions.get(action);
    }

    private void decrementRepeatAction(Action action) {
        if (!repeatActions.containsKey(action)) throw new IllegalArgumentException("Action is not present in repeatActions");
        repeatActions.put(action, getRepeatActionCount(action) - 1);
    }

    private void addRepeatAction(Action action, int startingCount) {
        repeatActions.put(action, startingCount);
    }

    private Action getRepeatActionMatch(Action action) {
        for (Action repeatAction : repeatActions.keySet()) {
            if (repeatAction.isRepeatMatch(action)) {
                return repeatAction;
            }
        }
        return null;
    }

}
