package com.github.finley243.adventureengine.action;

public abstract class ActionMove extends Action {

    @Override
    public int repeatCount() {
        return 1;
    }

    @Override
    public boolean isRepeatMatch(Action action) {
        return action instanceof ActionMove;
    }

    @Override
    public boolean isBlockedMatch(Action action) {
        return action instanceof ActionMove;
    }

}
