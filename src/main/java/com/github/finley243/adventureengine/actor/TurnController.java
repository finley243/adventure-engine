package com.github.finley243.adventureengine.actor;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.ai.Pathfinder;
import com.github.finley243.adventureengine.event.SensoryEventDispatcher;
import com.github.finley243.adventureengine.menu.MenuManager;
import com.github.finley243.adventureengine.quest.QuestManager;
import com.github.finley243.adventureengine.script.ScriptRuntime;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class TurnController {

    protected final Actor actor;
    protected final SensoryEventDispatcher sensoryEventDispatcher;
    protected final MenuManager menuManager;

    private final Map<Action, Integer> repeatActions;
    private int actionPointsUsed;

    public TurnController(Actor actor, SensoryEventDispatcher sensoryEventDispatcher, MenuManager menuManager) {
        this.actor = actor;
        this.sensoryEventDispatcher = sensoryEventDispatcher;
        this.menuManager = menuManager;
        this.repeatActions = new HashMap<>();
    }

    public void takeTurn(Pathfinder pathfinder, ScriptRuntime scriptRuntime, QuestManager questManager) {
        if (!actor.isEnabled() || actor.isDead()) {
            return;
        }
        if (actor.isSleeping()) {
            actor.updateSleep();
            return;
        }
        actor.getEffectComponent().onStartRound();
        actor.getInventory().onStartRound();
        onStartTurn(scriptRuntime);
        actionPointsUsed = 0;
        repeatActions.clear();
        actor.setTurnActive();
        Action lastAction = null;
        int repeatActionCount = 0;
        while (canContinueTurn()) {
            questManager.update();
            onPreAction(scriptRuntime, pathfinder);
            List<Action> actionChoices = actor.availableActions(pathfinder);
            applyActionConstraints(actionChoices);
            if (actionChoices.isEmpty()) {
                actor.endTurn();
                break;
            }
            Action selectedAction = selectAction(actionChoices);
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

    protected abstract void onPreAction(ScriptRuntime scriptRuntime, Pathfinder pathfinder);

    protected abstract void onPostAction(Action action);

    protected abstract void onStartTurn(ScriptRuntime scriptRuntime);

    protected abstract Action selectAction(List<Action> actions);

    private boolean canContinueTurn() {
        return !actor.isTurnEnded() && actor.isEnabled() && actor.isActive();
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
