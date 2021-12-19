package com.github.finley243.adventureengine.event;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;

import java.util.List;

public class ActionMenuEvent {

    private final List<Action> actions;
    private final Actor actor;

    public ActionMenuEvent(List<Action> actions, Actor actor) {
        this.actions = actions;
        this.actor = actor;
    }

    public List<Action> getActions() {
        return actions;
    }

    public Actor getActor() {
        return actor;
    }

}
