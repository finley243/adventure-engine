package com.github.finley243.adventureengine.action.reaction;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;

import java.util.concurrent.ThreadLocalRandom;

public abstract class ActionReaction extends Action {

    @Override
    public void choose(Actor subject) {}

    public boolean checkSuccess(Actor subject) {
        return ThreadLocalRandom.current().nextFloat() < chance(subject);
    }

    public abstract float chance(Actor subject);

    public abstract float damageMult();

}
