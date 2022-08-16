package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.actor.Actor;

import java.util.Collection;
import java.util.concurrent.ThreadLocalRandom;

public abstract class ActionRandomEach<T> extends Action {

    private final Collection<T> collection;

    public ActionRandomEach(ActionDetectionChance detectionChance, Collection<T> collection) {
        super(detectionChance);
        this.collection = collection;
    }

    @Override
    public void choose(Actor subject, int repeatActionCount) {
        boolean continueAfterStart = onStart(subject, repeatActionCount);
        if(continueAfterStart) {
            for (T target : collection) {
                if (ThreadLocalRandom.current().nextFloat() < chance(subject, target)) {
                    onSuccess(subject, target, repeatActionCount);
                } else {
                    onFail(subject, target, repeatActionCount);
                }
            }
        }
        onEnd(subject, repeatActionCount);
    }

    public String getChanceTag(Actor subject) {
        float minChance = Float.MAX_VALUE;
        float maxChance = Float.MIN_VALUE;
        for (T target : collection) {
            float currentChance = chance(subject, target);
            if (currentChance < minChance) {
                minChance = currentChance;
            }
            if (currentChance > maxChance) {
                maxChance = currentChance;
            }
        }
        if (minChance == maxChance) {
            return ((int) Math.ceil(minChance * 100)) + "%";
        } else {
            return ((int) Math.ceil(minChance * 100)) + "-" + ((int) Math.ceil(maxChance * 100)) + "%";
        }
    }

    /** If onStart returns false, the action will not process random success (onSuccess/onFail will be skipped) */
    public boolean onStart(Actor subject, int repeatActionCount) {
        return true;
    }

    public void onEnd(Actor subject, int repeatActionCount) {}

    public abstract void onSuccess(Actor subject, T target, int repeatActionCount);

    public abstract void onFail(Actor subject, T target, int repeatActionCount);

    public abstract float chance(Actor subject, T target);

}
