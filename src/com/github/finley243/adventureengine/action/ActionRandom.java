package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.actor.Actor;

import java.util.concurrent.ThreadLocalRandom;

public abstract class ActionRandom extends Action {

    @Override
    public void choose(Actor subject) {
        boolean continueAfterStart = onStart(subject);
        if(continueAfterStart) {
            if (ThreadLocalRandom.current().nextFloat() < chance(subject)) {
                onSuccess(subject);
            } else {
                onFail(subject);
            }
        }
    }

    public String getChanceTag(Actor subject) {
        return ((int) Math.ceil(chance(subject)*100)) + "%";
    }

    /** If onStart returns false, the action will not process random success (onSuccess/onFail will be skipped) */
    public abstract boolean onStart(Actor subject);

    public abstract void onSuccess(Actor subject);

    public abstract void onFail(Actor subject);

    public abstract float chance(Actor subject);

}
