package com.github.finley243.adventureengine.actor.component;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Faction;
import com.github.finley243.adventureengine.actor.ai.AreaTarget;
import com.github.finley243.adventureengine.actor.ai.UtilityUtils;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.item.ItemWeapon;

import java.util.*;

public class TargetingComponent {

    private static final int TURNS_DETECTED_UNTIL_COMBAT = 2;
    private static final int TURNS_UNTIL_END_COMBAT = 4;

    private final Actor subject;
    // Value is number of turns the actor has been detected
    private final Map<Actor, Integer> detected;

    private final Map<Actor, Combatant> combatants;

    private Actor followTarget;

    public TargetingComponent(Actor subject) {
        this.subject = subject;
        detected = new HashMap<>();
        combatants = new HashMap<>();
    }

    public void clear() {
        detected.clear();
        combatants.clear();
    }

    // Executed at the beginning of subject's turn
    public void updateTurn() {
        Set<Actor> visibleActors = subject.getVisibleActors();
        // Remove non-visible actors from detected
        for(Actor actor : detected.keySet()) {
            if(!visibleActors.contains(actor)) {
                detected.remove(actor);
            }
        }
        // Add remaining visible actors to detected and increment existing actors
        // TODO - Add allied target adding? (only for active combatants, not detected targets)
        for(Actor actor : visibleActors) {
            if(!combatants.containsKey(actor)) {
                if (detected.containsKey(actor)) {
                    int newValue = detected.get(actor) + 1;
                    if (newValue == TURNS_DETECTED_UNTIL_COMBAT) {
                        subject.triggerScript("on_detect_target");
                        addCombatant(actor);
                    } else {
                        if(!actor.isDead()) {
                            detected.put(actor, newValue);
                        }
                    }
                } else {
                    if(!actor.isDead()) {
                        if((subject.getArea().getRoom().getOwnerFaction() != null && subject.game().data().getFaction(subject.getArea().getRoom().getOwnerFaction()).getRelationTo(actor.getFaction().getID()) != Faction.FactionRelation.ASSIST) ||
                                (subject.getArea().getOwnerFaction() != null && subject.game().data().getFaction(subject.getArea().getOwnerFaction()).getRelationTo(actor.getFaction().getID()) != Faction.FactionRelation.ASSIST)) {
                            subject.triggerScript("on_notice_target_trespassing");
                            detected.put(actor, 0);
                        } else if((subject.getFaction().getRelationTo(actor.getFaction().getID())) == Faction.FactionRelation.HOSTILE) {
                            subject.triggerScript("on_notice_target_faction");
                            detected.put(actor, 0);
                        }
                    }
                }
            }
        }
        for(Combatant combatant : combatants.values()) {
            combatant.turnsUntilRemove -= 1;
        }
    }

    // Executed before each action during subject's turn
    public void update() {
        boolean startEmpty = combatants.isEmpty();
        for(Iterator<Actor> itr = combatants.keySet().iterator(); itr.hasNext();) {
            Actor actor = itr.next();
            Combatant combatant = combatants.get(actor);
            if (combatant.areaTarget == null) {
                combatant.areaTarget = new AreaTarget(idealAreas(combatant.lastKnownArea), 0.0f, true, false, false);
                subject.addPursueTarget(combatant.areaTarget);
            }
            if (subject.canSee(actor)) {
                combatant.lastKnownArea = actor.getArea();
                combatant.turnsUntilRemove = TURNS_UNTIL_END_COMBAT;
                combatant.areaTarget.setTargetAreas(idealAreas(combatant.lastKnownArea));
                combatant.areaTarget.setShouldFlee(UtilityUtils.shouldMoveAwayFrom(subject, actor));
                combatant.areaTarget.setIsActive(UtilityUtils.shouldActivatePursueTarget(subject, actor));
                combatant.areaTarget.setTargetUtility(UtilityUtils.getPursueTargetUtility(subject, actor));
            } else {
                combatant.areaTarget.setTargetAreas(idealAreas(combatant.lastKnownArea));
                combatant.areaTarget.setTargetUtility(UtilityUtils.getPursueInvisibleTargetUtility());
            }
            if(actor.isDead() || combatant.turnsUntilRemove <= 0) {
                combatant.areaTarget.markForRemoval();
                itr.remove();
            }
        }
        if(!startEmpty && combatants.isEmpty()) {
            subject.triggerScript("on_combat_end");
        }
    }

    public void updateCombatantArea(Actor actor, Area area) {
        if(combatants.containsKey(actor)) {
            combatants.get(actor).lastKnownArea = area;
        }
    }

    public void addCombatant(Actor actor) {
        if(combatants.isEmpty()) {
            subject.triggerScript("on_combat_start");
        }
        detected.remove(actor);
        if(!combatants.containsKey(actor)) {
            combatants.put(actor, new Combatant(actor.getArea()));
        }
    }

    public boolean isCombatant(Actor actor){
        return combatants.containsKey(actor);
    }

    public boolean hasCombatants() {
        return !combatants.isEmpty();
    }

    public boolean hasCombatantsInArea(Area area) {
        for(Combatant combatant : combatants.values()) {
            if(combatant.lastKnownArea.equals(area)) {
                return true;
            }
        }
        return false;
    }

    public Set<Actor> getCombatants() {
        return combatants.keySet();
    }

    public Area getLastKnownArea(Actor target) {
        if(subject.canSee(target)) {
            return target.getArea();
        } else if(combatants.containsKey(target)) {
            return combatants.get(target).lastKnownArea;
        } else {
            return null;
        }
    }

    private Set<Area> idealAreas(Area origin) {
        int idealDistanceMin = 0;
        int idealDistanceMax = 0;
        if(subject.equipmentComponent().hasRangedWeaponEquipped()) {
            ItemWeapon weapon = (ItemWeapon) subject.equipmentComponent().getEquippedItem();
            idealDistanceMin = weapon.getRangeMin();
            idealDistanceMax = weapon.getRangeMax();
        }
        if(idealDistanceMax == 0) {
            Set<Area> idealAreas = new HashSet<>();
            idealAreas.add(origin);
            return idealAreas;
        }
        return origin.visibleAreasInRange(idealDistanceMin, idealDistanceMax);
    }

    private static class Combatant {
        public AreaTarget areaTarget;
        public Area lastKnownArea;
        public int turnsUntilRemove;

        public Combatant(Area lastKnownArea) {
            this.lastKnownArea = lastKnownArea;
            this.turnsUntilRemove = TURNS_UNTIL_END_COMBAT;
        }
    }

}
