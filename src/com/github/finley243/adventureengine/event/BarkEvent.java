package com.github.finley243.adventureengine.event;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Bark;

public class BarkEvent implements QueuedEvent {

    private final Actor actor;
    private final String trigger;
    private final Context context;

    public BarkEvent(Actor actor, String trigger, Context context) {
        this.actor = actor;
        this.trigger = trigger;
        this.context = context;
    }

    @Override
    public void execute(Game game) {
        if (actor.isActive()) {
            Bark bark = actor.getBark(trigger);
            if (bark != null) {
                bark.trigger(context);
            }
        }
        game.eventQueue().executeNext();
    }

}
