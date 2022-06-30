package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.actor.Actor;

import java.util.concurrent.ThreadLocalRandom;

public abstract class ActionRandom extends Action {

    public ActionRandom(ActionDetectionChance detectionChance) {
        super(detectionChance);
    }

    @Override
    public void choose(Actor subject, int repeatActionCount) {
        boolean continueAfterStart = onStart(subject, repeatActionCount);
        if(continueAfterStart) {
            if (ThreadLocalRandom.current().nextFloat() < chance(subject)) {
                onSuccess(subject, repeatActionCount);
            } else {
                onFail(subject, repeatActionCount);
            }
        }
        onEnd(subject, repeatActionCount);
    }

    public String getChanceTag(Actor subject) {
        return ((int) Math.ceil(chance(subject)*100)) + "%";
    }

    /** If onStart returns false, the action will not process random success (onSuccess/onFail will be skipped) */
    public boolean onStart(Actor subject, int repeatActionCount) {
        return true;
    }

    public void onEnd(Actor subject, int repeatActionCount) {}

    public abstract void onSuccess(Actor subject, int repeatActionCount);

    public abstract void onFail(Actor subject, int repeatActionCount);

    public abstract float chance(Actor subject);

}
