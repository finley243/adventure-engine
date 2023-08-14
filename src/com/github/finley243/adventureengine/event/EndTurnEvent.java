package com.github.finley243.adventureengine.event;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;

public class EndTurnEvent implements QueuedEvent {

    public final Actor actor;

    public EndTurnEvent(Actor actor) {
        this.actor = actor;
    }

    @Override
    public void execute(Game game) {
        game.onEndTurn(actor);
    }

}
