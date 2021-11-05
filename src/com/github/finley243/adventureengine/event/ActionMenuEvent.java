package com.github.finley243.adventureengine.event;

import com.github.finley243.adventureengine.action.Action;

import java.util.List;

public class ActionMenuEvent {

    private List<Action> actions;

    public ActionMenuEvent(List<Action> actions) {
        this.actions = actions;
    }

    public List<Action> getActions() {
        return actions;
    }

}
