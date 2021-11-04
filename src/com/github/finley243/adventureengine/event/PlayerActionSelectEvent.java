package com.github.finley243.adventureengine.event;

import com.github.finley243.adventureengine.action.Action;

public class PlayerActionSelectEvent {

    private final Action action;

    public PlayerActionSelectEvent(Action action) {
        this.action = action;
    }

    public Action getAction() {
        return action;
    }

}
