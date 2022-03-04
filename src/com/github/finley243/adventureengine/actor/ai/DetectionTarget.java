package com.github.finley243.adventureengine.actor.ai;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.item.ItemWeapon;

import java.util.HashSet;
import java.util.Set;

public class DetectionTarget {

    // Temporary, will be replaced with different values depending on actor alert state
    /*public static final int TURNS_BEFORE_DETECTED = 2;

    private final Actor targetActor;

    private boolean markForRemoval;
    private int turnsDetected;

    public DetectionTarget(Actor actor) {
        this.targetActor = actor;
    }

    public void markForRemoval() {
        markForRemoval = true;
    }

    public void nextTurn(Actor subject) {
        if(subject.canSee(targetActor)) {
            turnsDetected++;
            if(turnsDetected == TURNS_BEFORE_DETECTED) {
                subject.triggerScript("on_detect_target");
                subject.addCombatTarget(targetActor);
            } else {
                subject.triggerScript("on_notice_target");
            }
        }
    }

    public void update(Actor subject) {
        if(!subject.canSee(targetActor)) {
            markForRemoval();
        }
    }

    public boolean shouldRemove() {
        return !targetActor.isActive() || markForRemoval;
    }

    public Actor getTargetActor() {
        return targetActor;
    }

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof ActorTarget)) {
            return false;
        } else {
            return this.getTargetActor().equals(((ActorTarget) other).getTargetActor());
        }
    }

    @Override
    public int hashCode() {
        return getTargetActor().hashCode();
    }*/

}
