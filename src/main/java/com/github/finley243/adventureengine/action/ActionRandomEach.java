package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.MathUtils;
import com.github.finley243.adventureengine.actor.Actor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class ActionRandomEach<T> extends Action {

    private final Collection<T> collection;

    public ActionRandomEach(Collection<T> collection) {
        //if (collection.isEmpty()) throw new IllegalArgumentException("ActionRandomEach collection cannot be empty");
        this.collection = collection;
    }

    @Override
    public void choose(Actor subject, int repeatActionCount) {
        boolean continueAfterStart = onStart(subject, repeatActionCount);
        if (continueAfterStart) {
            if (MathUtils.randomCheck(chanceOverall(subject))) {
                List<ComputedTarget<T>> computedTargets = new ArrayList<>();
                List<T> succeededTargets = new ArrayList<>();
                List<T> failedTargets = new ArrayList<>();
                for (T target : collection) {
                    if (MathUtils.randomCheck(chance(subject, target))) {
                        computedTargets.add(new ComputedTarget<>(target, true));
                        succeededTargets.add(target);
                    } else {
                        computedTargets.add(new ComputedTarget<>(target, false));
                        failedTargets.add(target);
                    }
                }
                onSuccessOverall(subject, repeatActionCount, succeededTargets, failedTargets);
                for (ComputedTarget<T> computedTarget : computedTargets) {
                    if (computedTarget.success()) {
                        onSuccess(subject, computedTarget.target(), repeatActionCount);
                    } else {
                        onFail(subject, computedTarget.target(), repeatActionCount);
                    }
                }
            } else {
                onFailOverall(subject, repeatActionCount);
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

    public abstract void onSuccessOverall(Actor subject, int repeatActionCount, List<T> targetsSuccess, List<T> targetsFail);

    public abstract void onFailOverall(Actor subject, int repeatActionCount);

    public abstract float chance(Actor subject, T target);

    public abstract float chanceOverall(Actor subject);

    private record ComputedTarget<T>(T target, boolean success) {}

}
