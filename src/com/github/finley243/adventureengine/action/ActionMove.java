package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.world.environment.Area;

public abstract class ActionMove extends Action {

    @Override
    public int repeatCount(Actor subject) {
        return Actor.MOVES_PER_TURN;
    }

    @Override
    public boolean isRepeatMatch(Action action) {
        return action instanceof ActionMove;
    }

    @Override
    public boolean isBlockedMatch(Action action) {
        return action instanceof ActionMove;
    }

    public abstract Area getDestinationArea();

}
