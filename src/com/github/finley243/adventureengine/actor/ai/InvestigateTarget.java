package com.github.finley243.adventureengine.actor.ai;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.world.environment.Area;

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
            pursueTarget = new PursueTarget(targetArea, UtilityUtils.INVESTIGATE_NOISE_UTILITY, true, false);
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