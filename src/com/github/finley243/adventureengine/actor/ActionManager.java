package com.github.finley243.adventureengine.actor;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionCrouch;
import com.github.finley243.adventureengine.action.ActionCrouchStop;
import com.github.finley243.adventureengine.action.ActionEnd;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.item.Item;
import com.github.finley243.adventureengine.world.item.ItemApparel;
import com.github.finley243.adventureengine.world.object.WorldObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActionManager {

    public static void takeTurn(Actor actor, int actionPoints) {
        if(!actor.isActive() || !actor.isEnabled()) return;
        Map<Action, Integer> blockedActions = new HashMap<>();
        actor.effectComponent().onStartTurn();
        actor.controller().onStartTurn();
        boolean endTurn = false;
        while(!endTurn) {
            actor.controller().onStartAction();
            List<Action> availableActions = availableActions(actor, blockedActions);
            for(Action action : availableActions) {
                if(actionPoints < action.actionPoints()) {
                    action.disable();
                }
            }
            Action chosenAction = actor.controller().chooseAction(availableActions);
            actionPoints -= chosenAction.actionPoints();
            boolean actionIsBlocked = false;
            for(Action repeatAction : blockedActions.keySet()) {
                if(repeatAction.isRepeatMatch(chosenAction)) {
                    int countRemaining = blockedActions.get(repeatAction) - 1;
                    blockedActions.put(repeatAction, countRemaining);
                    actionIsBlocked = true;
                    break;
                }
            }
            if(!actionIsBlocked && chosenAction.repeatCount() > 0) {
                blockedActions.put(chosenAction, chosenAction.repeatCount() - 1);
            }
            chosenAction.choose(this);
        }
    }

    private static List<Action> availableActions(Actor actor, Map<Action, Integer> blockedActions) {
        List<Action> actions = new ArrayList<>();
        if(actor.hasEquippedItem()) {
            actions.addAll(actor.getEquippedItem().equippedActions(actor));
        }
        for(Actor current : actor.getArea().getActors()) {
            actions.addAll(current.localActions(actor));
        }
        for(Area nearArea : actor.getArea().getNearAreas()) {
            for(Actor current : nearArea.getActors()) {
                actions.addAll(current.adjacentActions(actor));
            }
        }
        for(Actor current : actor.getVisibleActors()) {
            actions.addAll(current.remoteActions(actor));
        }
        for(WorldObject current : actor.getArea().getObjects()) {
            actions.addAll(current.localActions(actor));
        }
        for(Area nearArea : actor.getArea().getNearAreas()) {
            for(WorldObject current : nearArea.getObjects()) {
                actions.addAll(current.adjacentActions(actor));
            }
        }
        for(WorldObject current : actor.getVisibleObjects()) {
            actions.addAll(current.remoteActions(actor));
        }
        if(actor.isUsingObject()) {
            actions.addAll(actor.getUsingObject().usingActions());
        }
        if(actor.canMove()) {
            actions.addAll(actor.getArea().getMoveActions());
        }
        for(Item item : actor.inventory().getUniqueItems()) {
            actions.addAll(item.inventoryActions(actor));
        }
        for(ItemApparel item : actor.apparelManager().getEquippedItems()) {
            actions.addAll(item.equippedActions(actor));
        }
        if(actor.isCrouching()) {
            actions.add(new ActionCrouchStop());
        } else {
            actions.add(new ActionCrouch());
        }
        for(Action currentAction : actions) {
            boolean isBlocked = false;
            for (Action blockedAction : blockedActions.keySet()) {
                if (!(blockedActions.get(blockedAction) > 0 && blockedAction.isRepeatMatch(currentAction)) && blockedAction.isBlockedMatch(currentAction)) {
                    isBlocked = true;
                    break;
                }
            }
            if (isBlocked) {
                currentAction.disable();
            }
        }
        actions.add(new ActionEnd());
        return actions;
    }

}
