package com.github.finley243.adventureengine.actor.ai;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.world.environment.Area;

import java.util.HashSet;
import java.util.Set;

public class InvestigateTarget {

    public static final int TURNS_BEFORE_END_SEARCH = 2;

    private Area targetArea;
    private int turnsUntilRemove;
    private AreaTarget areaTarget;

    public InvestigateTarget() {
        this.targetArea = null;
        this.areaTarget = null;
        this.turnsUntilRemove = TURNS_BEFORE_END_SEARCH;
    }

    /**
     * Progresses the investigation timer at the start of each turn
     * @param subject The investigating actor
     */
    public void nextTurn(Actor subject) {
        if(subject.getArea() == targetArea) {
            turnsUntilRemove--;
            if(turnsUntilRemove <= 0) {
                areaTarget.markForRemoval();
                areaTarget = null;
                targetArea = null;
                subject.triggerScript("on_investigate_end", subject);
            }
        }
    }

    public void update(Actor subject) {
        if(areaTarget == null && targetArea != null) {
            areaTarget = new AreaTarget(targetArea, UtilityUtils.INVESTIGATE_NOISE_UTILITY, true);
            subject.addPursueTarget(areaTarget);
        }
        if(subject.getArea() == targetArea) {
            turnsUntilRemove--;
            if(turnsUntilRemove <= 0) {
                areaTarget.markForRemoval();
                areaTarget = null;
                targetArea = null;
            }
        }
    }

    public void setTargetArea(Area targetArea) {
        this.targetArea = targetArea;
        if(areaTarget != null) {
            areaTarget.markForRemoval();
            areaTarget = null;
        }
        turnsUntilRemove = TURNS_BEFORE_END_SEARCH;
    }

}