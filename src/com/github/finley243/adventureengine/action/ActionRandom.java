package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.actor.Actor;

import java.util.concurrent.ThreadLocalRandom;

public abstract class ActionRandom extends Action {

    @Override
    public void choose(Actor subject) {
        onStart(subject);
        if(ThreadLocalRandom.current().nextFloat() < chance(subject)) {
            onSuccess(subject);
        } else {
            onFail(subject);
        }
    }

    public abstract void onStart(Actor subject);

    public abstract void onSuccess(Actor subject);

    public abstract void onFail(Actor subject);

    public abstract float chance(Actor subject);

}
