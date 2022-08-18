package com.github.finley243.adventureengine.actor.component;

import com.github.finley243.adventureengine.MathUtils;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Faction;
import com.github.finley243.adventureengine.actor.ai.AreaTarget;
import com.github.finley243.adventureengine.actor.ai.UtilityUtils;
import com.github.finley243.adventureengine.load.SaveData;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.item.ItemWeapon;
import com.github.finley243.adventureengine.world.environment.AreaLink;

import java.util.*;

public class TargetingComponent {

    private static final int TURNS_UNTIL_END_COMBAT = 8;
    private static final AlertState DEFAULT_ALERT_STATE = AlertState.AWARE;

    public enum AlertState {
        DISTRACTED(4),
        AWARE(2),
        ALERT(1);

        public final int turnsToDetect;
        AlertState(int turnsToDetect) {
            this.turnsToDetect = turnsToDetect;
        }
    }

    private final Actor actor;
    // Value is number of turns the actor has been detected
    private final Map<Actor, Integer> detectionCounters;
    private final Map<Actor, Combatant> combatants;
    // TODO - Add system for removing non-combatants after they are not visible for several turns, just like combatants
    private final Set<Actor> nonCombatants;
    private final Set<Actor> deadActors;
    private AlertState alertState;

    public TargetingComponent(Actor actor) {
        this.actor = actor;
        detectionCounters = new HashMap<>();
        combatants = new HashMap<>();
        nonCombatants = new HashSet<>();
        deadActors = new HashSet<>();
        this.alertState = DEFAULT_ALERT_STATE;
    }

    public void setAlertState(AlertState state) {
        this.alertState = state;
    }

    public void clear() {
        detectionCounters.clear();
        combatants.clear();
        nonCombatants.clear();
        deadActors.clear();
    }

    // Executed at the beginning of subject's turn
    public void updateTurn() {
        // TODO - Implement new system for lowering/removing detection counters when actors are not seen for a period of time
        /*Set<Actor> visibleActors = actor.getVisibleActors();
        for(Actor actor : detectionCounters.keySet()) {
            if(!visibleActors.contains(actor)) {
                detectionCounters.remove(actor);
            }
        }*/
        for (Combatant combatant : combatants.values()) {
            combatant.turnsUntilRemove -= 1;
        }
    }

    // Executed before each action during subject's turn
    public void update() {
        boolean startEmpty = combatants.isEmpty();
        for(Iterator<Actor> itr = combatants.keySet().iterator(); itr.hasNext();) {
            Actor target = itr.next();
            Combatant combatant = combatants.get(target);
            if (combatant.areaTarget == null) {
                combatant.areaTarget = new AreaTarget(idealAreas(combatant.lastKnownArea), 0.0f, true);
                actor.addPursueTarget(combatant.areaTarget);
            }
            if (actor.canSee(target)) {
                combatant.lastKnownArea = target.getArea();
                combatant.turnsUntilRemove = TURNS_UNTIL_END_COMBAT;
            }
            combatant.areaTarget.setTargetAreas(idealAreas(combatant.lastKnownArea));
            combatant.areaTarget.setTargetUtility(UtilityUtils.getPursueTargetUtility(actor, target));
            if(target.isDead() || combatant.turnsUntilRemove <= 0) {
                combatant.areaTarget.markForRemoval();
                itr.remove();
            }
        }
        if(!startEmpty && combatants.isEmpty()) {
            actor.triggerScript("on_combat_end", actor);
        }
    }

    public void updateCombatantArea(Actor target, Area area) {
        if(combatants.containsKey(target)) {
            Combatant combatant = combatants.get(target);
            combatant.lastKnownArea = area;
            if (combatant.areaTarget != null) {
                combatant.areaTarget.setTargetAreas(idealAreas(combatant.lastKnownArea));
            }
        }
    }

    public void onVisibleAction(Action action, Actor subject) {
        if (isDetected(subject)) return;
        float detectionChance = getActionDetectionChance(action, subject);
        boolean detected = MathUtils.randomCheck(detectionChance);
        if (detected) {
            if (detectionCounters.containsKey(subject)) {
                int currentCount = detectionCounters.get(subject);
                detectionCounters.put(subject, currentCount + 1);
            } else {
                detectionCounters.put(subject, 1);
            }
            subject.onDetectionUpdate(actor, detectionCounters.get(subject), detectionThreshold());
            if (detectionCounters.get(subject) >= detectionThreshold()) {
                onDetected(subject);
            }
        }
    }

    private int detectionThreshold() {
        return alertState.turnsToDetect;
    }

    private void onDetected(Actor subject) {
        // TODO - Add allied target adding? Handle with bark communication? (only for active combatants, not detected targets)
        if ((actor.getArea().getRoom().getOwnerFaction() != null && actor.game().data().getFaction(actor.getArea().getRoom().getOwnerFaction()).getRelationTo(subject.getFaction().getID()) != Faction.FactionRelation.ASSIST) ||
                (actor.getArea().getOwnerFaction() != null && actor.game().data().getFaction(actor.getArea().getOwnerFaction()).getRelationTo(subject.getFaction().getID()) != Faction.FactionRelation.ASSIST)) {
            actor.triggerScript("on_detect_target_trespassing", subject);
            // TODO - Make trespassing cause a warning first (actor follows trespasser?), become hostile after a couple turns
            addCombatant(actor);
        } else if (actor.getFaction().getRelationTo(subject.getFaction().getID()) == Faction.FactionRelation.HOSTILE) {
            actor.triggerScript("on_detect_target_faction", subject);
            addCombatant(actor);
        } else {
            addNonCombatant(actor);
        }
    }

    // TODO - Needs to account for adding a combatant that is already in the non-combatant set
    public void addCombatant(Actor target) {
        if (combatants.isEmpty()) {
            actor.triggerScript("on_combat_start", target);
        }
        detectionCounters.remove(target);
        if (!combatants.containsKey(target)) {
            combatants.put(target, new Combatant(target.getArea()));
        }
    }

    private void addNonCombatant(Actor target) {
        detectionCounters.remove(target);
        nonCombatants.add(target);
    }

    public boolean isCombatant(Actor target){
        return combatants.containsKey(target);
    }

    public boolean isNonCombatant(Actor target) {
        return nonCombatants.contains(target);
    }

    public boolean isDetected(Actor target) {
        return isCombatant(target) || isNonCombatant(target);
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
        if(combatants.containsKey(target)) {
            return combatants.get(target).lastKnownArea;
        } else {
            return null;
        }
    }

    public float getActionDetectionChance(Action action, Actor subject) {
        switch (action.detectionChance()){
            case LOW:
                return MathUtils.chanceLinearSkillInverted(subject, Actor.Skill.STEALTH, 0.01f, 0.50f);
            case HIGH:
                return MathUtils.chanceLinearSkillInverted(subject, Actor.Skill.STEALTH, 0.10f, 0.95f);
            case NONE:
            default:
                return 0.0f;
        }
    }

    private AreaLink.DistanceCategory idealDistance() {
        if (actor.equipmentComponent() != null && actor.equipmentComponent().hasEquippedItem() && actor.equipmentComponent().getEquippedItem() instanceof ItemWeapon) {
            return ((ItemWeapon) actor.equipmentComponent().getEquippedItem()).getRange();
        } else {
            return AreaLink.DistanceCategory.NEAR;
        }
    }

    private Set<Area> idealAreas(Area origin) {
        AreaLink.DistanceCategory idealDistance = idealDistance();
        if (idealDistance == AreaLink.DistanceCategory.NEAR) {
            Set<Area> idealAreas = new HashSet<>();
            idealAreas.add(origin);
            return idealAreas;
        }
        return origin.visibleAreasInRange(actor, idealDistance);
    }

    public void loadState(SaveData data) {
        if (data.getParameter().equals("targeting")) {
            for (SaveData subData : data.getValueMulti()) {
                switch (subData.getParameter()) {
                    case "detected":
                        detectionCounters.put(actor.game().data().getActor(subData.getValueString()), subData.getValueInt());
                        break;
                    case "combatant":
                        Actor actor = null;
                        Combatant combatant = new Combatant(null);
                        for (SaveData combatantData : subData.getValueMulti()) {
                            switch (combatantData.getParameter()) {
                                case "actor":
                                    actor = this.actor.game().data().getActor(combatantData.getValueString());
                                    break;
                                case "lastKnownArea":
                                    combatant.lastKnownArea = this.actor.game().data().getArea(combatantData.getValueString());
                                    break;
                                case "turnsUntilRemove":
                                    combatant.turnsUntilRemove = combatantData.getValueInt();
                            }
                        }
                        if (actor != null) {
                            combatants.put(actor, combatant);
                        }
                        break;
                }
            }
        }
    }

    public List<SaveData> saveState() {
        List<SaveData> state = new ArrayList<>();
        for (Actor actor : detectionCounters.keySet()) {
            state.add(new SaveData(null, null, "detected", actor.getID(), detectionCounters.get(actor)));
        }
        for (Actor actor : combatants.keySet()) {
            Combatant combatant = combatants.get(actor);
            List<SaveData> combatantData = new ArrayList<>();
            combatantData.add(new SaveData(null, null, "actor", actor.getID()));
            combatantData.add(new SaveData(null, null, "lastKnownArea", combatant.lastKnownArea.getID()));
            combatantData.add(new SaveData(null, null, "turnsUntilRemove", combatant.turnsUntilRemove));
            state.add(new SaveData(null, null, "combatant", combatantData));
        }
        return state;
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
