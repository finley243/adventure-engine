package com.github.finley243.adventureengine.actor;

import com.github.finley243.adventureengine.Data;
import com.github.finley243.adventureengine.actor.ai.CombatTarget;
import com.github.finley243.adventureengine.actor.ai.InvestigateTarget;
import com.github.finley243.adventureengine.actor.ai.PursueTarget;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class TargetManager {

    private final Set<CombatTarget> combatTargets;
    private final Set<PursueTarget> pursueTargets;
    private final InvestigateTarget investigateTarget;
    private final Actor actor;

    public TargetManager(Actor actor) {
        this.actor = actor;
        combatTargets = new HashSet<>();
        pursueTargets = new HashSet<>();
        investigateTarget = new InvestigateTarget();
    }

    public boolean isCombatTarget(Actor actor) {
        for(CombatTarget target : combatTargets) {
            if(target.getTargetActor() == actor) {
                return true;
            }
        }
        return false;
    }

    public void addCombatTarget(Actor actor) {
        combatTargets.add(new CombatTarget(actor));
    }

    public Set<CombatTarget> getCombatTargets() {
        return combatTargets;
    }

    public void addPursueTarget(PursueTarget target) {
        pursueTargets.add(target);
    }

    public Set<PursueTarget> getPursueTargets() {
        return pursueTargets;
    }

    public void onStartTurn() {
        updateCombatTargetsTurn();
        investigateTarget.nextTurn(actor);
    }

    public void onStartAction() {
        generateCombatTargets();
        updatePursueTargets();
        updateCombatTargets();
        investigateTarget.update(actor);
    }

    private void generateCombatTargets() {
        for(Actor current : actor.getVisibleActors()) {
            if(current != actor && !actor.isDead()) {
                if(actor.getFaction().getRelationTo(current.getFaction().getID()) == Faction.FactionRelation.ENEMY) {
                    if(!isCombatTarget(current)) {
                        addCombatTarget(current);
                    }
                } else if(actor.getFaction().getRelationTo(actor.getFaction().getID()) == Faction.FactionRelation.FRIEND) {
                    for(CombatTarget allyTarget : current.getCombatTargets()) {
                        if(!isCombatTarget(allyTarget.getTargetActor())) {
                            addCombatTarget(allyTarget.getTargetActor());
                        }
                    }
                } else if(actor.getArea().getRoom().getOwnerFaction() != null && Data.getFaction(actor.getArea().getRoom().getOwnerFaction()).getRelationTo(current.getFaction().getID()) != Faction.FactionRelation.FRIEND) {
                    if(!isCombatTarget(current)) {
                        addCombatTarget(current);
                    }
                }
            }
        }
    }

    private void updateCombatTargetsTurn() {
        for(CombatTarget target : combatTargets) {
            target.nextTurn();
        }
    }

    private void updateCombatTargets() {
        Iterator<CombatTarget> itr = combatTargets.iterator();
        while(itr.hasNext()) {
            CombatTarget target = itr.next();
            target.update(actor);
            if(target.shouldRemove()) {
                itr.remove();
            }
        }
    }

    private void updatePursueTargets() {
        Iterator<PursueTarget> itr = pursueTargets.iterator();
        while(itr.hasNext()) {
            PursueTarget target = itr.next();
            target.update(actor);
            if(target.shouldRemove()) {
                itr.remove();
            }
        }
    }

}
