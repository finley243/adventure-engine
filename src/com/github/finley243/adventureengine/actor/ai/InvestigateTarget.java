package com.github.finley243.adventureengine.actor.ai;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.world.environment.Area;

import java.util.HashSet;
import java.util.Set;

public class InvestigateTarget {

    public static final int TURNS_BEFORE_END_SEARCH = 2;

    private Area targetArea;
    private int turnsUntilRemove;
    private PursueTarget pursueTarget;

    public InvestigateTarget() {
        this.targetArea = null;
        this.pursueTarget = null;
        this.turnsUntilRemove = TURNS_BEFORE_END_SEARCH;
    }

    public void nextTurn(Actor subject) {
        if(subject.getArea() == targetArea) {
            turnsUntilRemove--;
            if(turnsUntilRemove <= 0) {
                pursueTarget.markForRemoval();
                pursueTarget = null;
                targetArea = null;
            }
        }
    }

    public void update(Actor subject) {
        if(pursueTarget == null && targetArea != null) {
            Set<Area> targetSet = new HashSet<>();
            targetSet.add(targetArea);
            pursueTarget = new PursueTarget(targetSet, UtilityUtils.INVESTIGATE_NOISE_UTILITY, true, false, false);
            subject.addPursueTarget(pursueTarget);
        }
        if(subject.getArea() == targetArea) {
            turnsUntilRemove--;
            if(turnsUntilRemove <= 0) {
                pursueTarget.markForRemoval();
                pursueTarget = null;
                targetArea = null;
            }
        }
    }

    public void setTargetArea(Area targetArea) {
        this.targetArea = targetArea;
        if(pursueTarget != null) {
            pursueTarget.markForRemoval();
            pursueTarget = null;
        }
        turnsUntilRemove = TURNS_BEFORE_END_SEARCH;
    }

}