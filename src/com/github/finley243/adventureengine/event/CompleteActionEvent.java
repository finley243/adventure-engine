package com.github.finley243.adventureengine.event;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;

public class CompleteActionEvent implements QueuedEvent {

    private final Actor actor;
    private final Action action;
    private final int repeatActionCount;

    public CompleteActionEvent(Actor actor, Action action, int repeatActionCount) {
        this.actor = actor;
        this.action = action;
        this.repeatActionCount = repeatActionCount;
    }

    @Override
    public void execute(Game game) {
        actor.onCompleteAction(action, repeatActionCount);
    }

}
