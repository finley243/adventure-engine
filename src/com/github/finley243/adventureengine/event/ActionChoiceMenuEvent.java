package com.github.finley243.adventureengine.event;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;

import java.util.List;

public class ActionChoiceMenuEvent implements QueuedEvent, ChoiceMenuEvent {

    private final Actor actor;
    private final Action lastAction;
    private final int repeatActionCount;
    private List<Action> actionChoices;

    public ActionChoiceMenuEvent(Actor actor, Action lastAction, int repeatActionCount) {
        this.actor = actor;
        this.lastAction = lastAction;
        this.repeatActionCount = repeatActionCount;
    }

    @Override
    public void execute(Game game) {
        actionChoices = actor.availableActions();
        if (actionChoices.isEmpty()) {
            game.eventQueue().executeNext();
            return;
        }
        if (actor.isPlayerControlled()) {
            game.menuManager().actionChoiceMenu(this, game, actor, actionChoices);
        } else {
            Action selectedAction = actor.chooseAIAction(actionChoices);
            actor.onSelectAction(selectedAction, lastAction, repeatActionCount);
            game.eventQueue().executeNext();
        }
    }

    @Override
    public void onChoiceMenuInput(int menuIndex) {
        actor.onSelectAction(actionChoices.get(menuIndex), lastAction, repeatActionCount);
        actor.game().eventQueue().executeNext();
    }

}
