package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.MathUtils;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.SensoryEventDispatcher;

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
    public void choose(Actor subject, int repeatActionCount, SensoryEventDispatcher sensoryEventDispatcher) {
        boolean continueAfterStart = onStart(game, subject, repeatActionCount);
        if (continueAfterStart) {
            if (MathUtils.randomCheck(chanceOverall(game, subject))) {
                List<ComputedTarget<T>> computedTargets = new ArrayList<>();
                List<T> succeededTargets = new ArrayList<>();
                List<T> failedTargets = new ArrayList<>();
                for (T target : collection) {
                    if (MathUtils.randomCheck(chance(game, subject, target))) {
                        computedTargets.add(new ComputedTarget<>(target, true));
                        succeededTargets.add(target);
                    } else {
                        computedTargets.add(new ComputedTarget<>(target, false));
                        failedTargets.add(target);
                    }
                }
                onSuccessOverall(game, subject, repeatActionCount, succeededTargets, failedTargets);
                for (ComputedTarget<T> computedTarget : computedTargets) {
                    if (computedTarget.success()) {
                        onSuccess(game, subject, computedTarget.target(), repeatActionCount);
                    } else {
                        onFail(game, subject, computedTarget.target(), repeatActionCount);
                    }
                }
            } else {
                onFailOverall(game, subject, repeatActionCount);
            }
        }
        onEnd(game, subject, repeatActionCount);
    }

    public String getChanceTag(Game game, Actor subject) {
        float minChance = Float.MAX_VALUE;
        float maxChance = Float.MIN_VALUE;
        for (T target : collection) {
            float currentChance = chance(game, subject, target);
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
    public boolean onStart(Game game, Actor subject, int repeatActionCount) {
        return true;
    }

    public void onEnd(Game game, Actor subject, int repeatActionCount) {}

    public abstract void onSuccess(Game game, Actor subject, T target, int repeatActionCount);

    public abstract void onFail(Game game, Actor subject, T target, int repeatActionCount);

    public abstract void onSuccessOverall(Game game, Actor subject, int repeatActionCount, List<T> targetsSuccess, List<T> targetsFail);

    public abstract void onFailOverall(Game game, Actor subject, int repeatActionCount);

    public abstract float chance(Game game, Actor subject, T target);

    public abstract float chanceOverall(Game game, Actor subject);

    private record ComputedTarget<T>(T target, boolean success) {}

}
