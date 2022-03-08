package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.actor.Actor;

public abstract class ActionMove extends Action {

    @Override
    public int repeatCount(Actor subject) {
        if(subject.isCrouching()) {
            return Actor.MOVES_PER_TURN_CROUCHED;
        } else {
            return Actor.MOVES_PER_TURN;
        }
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
