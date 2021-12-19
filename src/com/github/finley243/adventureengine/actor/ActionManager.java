package com.github.finley243.adventureengine.actor;

import com.github.finley243.adventureengine.action.Action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActionManager {

    /*public static void takeTurn(Actor actor, int actionPoints) {
        if(!actor.isActive() || !actor.isEnabled()) return;
        Map<Action, Integer> blockedActions = new HashMap<>();
        actor.effectComponent().onStartTurn();
        actor.updateCombatTargetsTurn();
        actor.investigateTarget.nextTurn(this);
        behaviorIdle.update(this);
        this.blockedActions.clear();
        this.endTurn = false;
        while(!endTurn) {
            generateCombatTargets();
            updatePursueTargets();
            updateCombatTargets();
            investigateTarget.update(this);
            List<Action> availableActions = availableActions(false);
            for(Action action : availableActions) {
                if(actionPoints < action.actionPoints()) {
                    action.disable();
                }
            }
            Action chosenAction = chooseAction(availableActions);
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
    }*/

}
