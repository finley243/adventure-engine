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

import java.util.*;

public class TargetingComponent {

    private static final int TURNS_UNTIL_END_COMBAT = 4;
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
    private final Set<Actor> nonCombatants;

    private Actor followTarget;
    private AlertState alertState;

    public TargetingComponent(Actor actor) {
        this.actor = actor;
        detectionCounters = new HashMap<>();
        combatants = new HashMap<>();
        nonCombatants = new HashSet<>();
        this.alertState = DEFAULT_ALERT_STATE;
    }

    public void setAlertState(AlertState state) {
        this.alertState = state;
    }

    public void clear() {
        detectionCounters.clear();
        combatants.clear();
    }

    // Executed at the beginning of subject's turn
    public void updateTurn() {
        Set<Actor> visibleActors = actor.getVisibleActors();
        // Remove non-visible actors from detected
        for(Actor actor : detectionCounters.keySet()) {
            if(!visibleActors.contains(actor)) {
                detectionCounters.remove(actor);
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
                actor.addPursueTarget(combatant.areaTarget);
            }
            if (actor.canSee(actor)) {
                combatant.lastKnownArea = actor.getArea();
                combatant.turnsUntilRemove = TURNS_UNTIL_END_COMBAT;
                combatant.areaTarget.setTargetAreas(idealAreas(combatant.lastKnownArea));
                combatant.areaTarget.setShouldFlee(UtilityUtils.shouldMoveAwayFrom(this.actor, actor));
                combatant.areaTarget.setIsActive(UtilityUtils.shouldActivatePursueTarget(this.actor, actor));
                combatant.areaTarget.setTargetUtility(UtilityUtils.getPursueTargetUtility(this.actor, actor));
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
            actor.triggerScript("on_combat_end");
        }
    }

    public void updateCombatantArea(Actor actor, Area area) {
        if(combatants.containsKey(actor)) {
            combatants.get(actor).lastKnownArea = area;
        }
    }

    public void onVisibleAction(Action action, Actor subject) {
        float detectionChance = getActionDetectionChance(action, subject);
        boolean detected = MathUtils.randomCheck(detectionChance);
        if (detected) {
            // TODO - Trigger onDetection function in actor (display updates for player-controlled actors)
            if (detectionCounters.containsKey(subject)) {
                int currentCount = detectionCounters.get(subject);
                detectionCounters.put(subject, currentCount + 1);
            } else {
                detectionCounters.put(subject, 1);
            }
            if (detectionCounters.get(subject) >= detectionThreshold()) {
                onDetected(subject);
            }
        }
    }

    private int detectionThreshold() {
        return alertState.turnsToDetect;
    }

    private void onDetected(Actor subject) {
        // TODO - Add allied target adding? (only for active combatants, not detected targets)
        if((this.actor.getArea().getRoom().getOwnerFaction() != null && this.actor.game().data().getFaction(this.actor.getArea().getRoom().getOwnerFaction()).getRelationTo(actor.getFaction().getID()) != Faction.FactionRelation.ASSIST) ||
                (this.actor.getArea().getOwnerFaction() != null && this.actor.game().data().getFaction(this.actor.getArea().getOwnerFaction()).getRelationTo(actor.getFaction().getID()) != Faction.FactionRelation.ASSIST)) {
            this.actor.triggerScript("on_detect_target_trespassing");
            addCombatant(actor);
        } else if(this.actor.getFaction().getRelationTo(actor.getFaction().getID()) == Faction.FactionRelation.HOSTILE) {
            this.actor.triggerScript("on_detect_target_faction");
            addCombatant(actor);
        } else {
            addNonCombatant(actor);
        }
    }

    public void addCombatant(Actor actor) {
        if (combatants.isEmpty()) {
            actor.triggerScript("on_combat_start");
        }
        detectionCounters.remove(actor);
        if (!combatants.containsKey(actor)) {
            combatants.put(actor, new Combatant(actor.getArea()));
        }
    }

    private void addNonCombatant(Actor actor) {
        detectionCounters.remove(actor);
        nonCombatants.add(actor);
    }

    public boolean isCombatant(Actor actor){
        return combatants.containsKey(actor);
    }

    public boolean isNonCombatant(Actor actor) {
        return nonCombatants.contains(actor);
    }

    public boolean isDetected(Actor actor) {
        return isCombatant(actor) || isNonCombatant(actor);
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
                return MathUtils.chanceLinearSkillInverted(subject, Actor.Skill.STEALTH, 0.05f, 0.80f);
            case NONE:
            default:
                return 0.0f;
        }
    }

    private Set<Area> idealAreas(Area origin) {
        int idealDistanceMin = 0;
        int idealDistanceMax = 0;
        if(actor.equipmentComponent().hasEquippedItem()) {
            ItemWeapon weapon = (ItemWeapon) actor.equipmentComponent().getEquippedItem();
            idealDistanceMin = weapon.getRangeMin();
            idealDistanceMax = weapon.getRangeMax();
        }
        if(idealDistanceMax == 0) {
            Set<Area> idealAreas = new HashSet<>();
            idealAreas.add(origin);
            return idealAreas;
        }
        Set<Area> idealAreas = origin.visibleAreasInRange(actor, idealDistanceMin, idealDistanceMax);
        while(idealAreas.isEmpty()) {
            if(idealDistanceMin >= 0) {
                idealDistanceMin -= 1;
            }
            idealDistanceMax += 1;
            idealAreas.addAll(origin.visibleAreasInRange(actor, idealDistanceMin, idealDistanceMin));
            idealAreas.addAll(origin.visibleAreasInRange(actor, idealDistanceMax, idealDistanceMax));
        }
        return idealAreas;
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
