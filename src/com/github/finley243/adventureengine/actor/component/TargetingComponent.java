package com.github.finley243.adventureengine.actor.component;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.MathUtils;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Faction;
import com.github.finley243.adventureengine.actor.ai.AreaTarget;
import com.github.finley243.adventureengine.actor.ai.UtilityUtils;
import com.github.finley243.adventureengine.item.ItemWeapon;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.environment.AreaLink;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TargetingComponent {

    private static final AlertState DEFAULT_ALERT_STATE = AlertState.AWARE;
    private static final int TRESPASSING_TURNS_UNTIL_HOSTILE = 2;

    public enum DetectionState {
        DETECTING(true, 8),
        TRESPASSING(true, 4),
        HOSTILE(false, 8),
        PASSIVE(false, 4),
        DEAD(false, -1);

        public final boolean updateOnTurn; // Only updates if visible
        public final int turnsUntilRemove; // If -1, will never remove while in this state
        DetectionState(boolean updateOnTurn, int turnsUntilRemove) {
            this.updateOnTurn = updateOnTurn;
            this.turnsUntilRemove = turnsUntilRemove;
        }
    }

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
    private final Map<Actor, DetectedActor> detectedActors;
    private AlertState alertState;

    public TargetingComponent(Actor actor) {
        this.actor = actor;
        this.detectedActors = new HashMap<>();
        this.alertState = DEFAULT_ALERT_STATE;
    }

    public void setAlertState(AlertState state) {
        this.alertState = state;
    }

    public void updateTurn() {
        Set<Actor> visibleActors = actor.getVisibleActors();
        Set<Actor> actorsToRemove = new HashSet<>();
        for (Map.Entry<Actor, DetectedActor> entry : detectedActors.entrySet()) {
            if (visibleActors.contains(entry.getKey())) {
                entry.getValue().lostVisualCounter = 0;
                entry.getValue().lastKnownArea = entry.getKey().getArea();
                if (entry.getValue().state.updateOnTurn) {
                    updateState(entry.getKey());
                }
            } else {
                if (entry.getValue().state.turnsUntilRemove != -1) {
                    entry.getValue().lostVisualCounter += 1;
                    if (entry.getValue().lostVisualCounter >= entry.getValue().state.turnsUntilRemove) {
                        actorsToRemove.add(entry.getKey());
                    }
                }
            }
        }
        for (Actor actorToRemove : actorsToRemove) {
            if (detectedActors.get(actorToRemove).areaTarget != null) {
                detectedActors.get(actorToRemove).areaTarget.markForRemoval();
            }
            detectedActors.remove(actorToRemove);
        }
    }

    public void update() {
        for (Actor visibleActor : actor.getVisibleActors()) {
            processDetectionEvent(visibleActor, getPassiveDetectionChance(visibleActor));
        }
        boolean startedInCombat = false;
        for (Map.Entry<Actor, DetectedActor> entry : detectedActors.entrySet()) {
            if (entry.getValue().state == DetectionState.HOSTILE) {
                startedInCombat = true;
                if (actor.canSee(entry.getKey())) {
                    entry.getValue().lastKnownArea = entry.getKey().getArea();
                }
                if (entry.getKey().isDead()) {
                    entry.getValue().state = DetectionState.DEAD;
                    entry.getValue().stateCounter = 0;
                    if (entry.getValue().areaTarget != null) {
                        entry.getValue().areaTarget.markForRemoval();
                        entry.getValue().areaTarget = null;
                    }
                } else if (entry.getValue().areaTarget == null) {
                    entry.getValue().areaTarget = new AreaTarget(idealAreas(entry.getValue().lastKnownArea), UtilityUtils.getPursueTargetUtility(actor, entry.getKey()), true);
                    actor.addPursueTarget(entry.getValue().areaTarget);
                } else {
                    entry.getValue().areaTarget.setTargetAreas(idealAreas(entry.getValue().lastKnownArea));
                    entry.getValue().areaTarget.setTargetUtility(UtilityUtils.getPursueTargetUtility(actor, entry.getKey()));
                }
            }
        }
        if (startedInCombat && !hasTargetsOfType(DetectionState.HOSTILE)) {
            actor.triggerScript("on_combat_end", actor);
            actor.triggerBark("on_combat_end", actor);
        }
    }

    public void updateTargetArea(Actor target, Area area) {
        if(detectedActors.containsKey(target)) {
            DetectedActor combatant = detectedActors.get(target);
            combatant.lastKnownArea = area;
            if (combatant.areaTarget != null) {
                combatant.areaTarget.setTargetAreas(idealAreas(combatant.lastKnownArea));
            }
        }
    }

    public void onVisibleAction(Action action, Actor subject) {
        processDetectionEvent(subject, getActionDetectionChance(action, subject));
        updateTargetArea(subject, subject.getArea());
        // TODO - Handle criminal action detection
    }

    private void processDetectionEvent(Actor subject, float detectionChance) {
        if (!detectedActors.containsKey(subject) || detectedActors.get(subject).state == DetectionState.DETECTING) {
            boolean detected = MathUtils.randomCheck(detectionChance);
            if (detected) {
                if (!detectedActors.containsKey(subject)) {
                    detectedActors.put(subject, new DetectedActor(DetectionState.DETECTING, subject.getArea()));
                }
                // TODO - Replace with detection phrase (different phrases depending on level of detection)
                //subject.onDetectionUpdate(subject, detectedActors.get(subject).stateCounter, detectionThreshold());
                updateState(subject);
            }
        }
    }

    private void updateState(Actor target) {
        detectedActors.get(target).stateCounter += 1;
        if (detectedActors.get(target).stateCounter >= getStateTriggerValue(detectedActors.get(target).state)) {
            detectedActors.get(target).stateCounter = 0;
            switch (detectedActors.get(target).state) {
                case DETECTING -> {
                    if (actor.isDead()) {
                        // TODO - Limit response to actors in allied faction?
                        actor.triggerScript("on_detect_dead", target);
                        actor.triggerBark("on_detect_dead", target);
                        detectedActors.get(target).state = DetectionState.DEAD;
                    } else if ((target.getArea().getRoom().getOwnerFaction() != null && actor.game().data().getFaction(target.getArea().getRoom().getOwnerFaction()).getRelationTo(target.getFaction().getID()) != Faction.FactionRelation.ASSIST) ||
                        (target.getArea().getOwnerFaction() != null && actor.game().data().getFaction(target.getArea().getOwnerFaction()).getRelationTo(target.getFaction().getID()) != Faction.FactionRelation.ASSIST)) {
                        // TODO - Limit trespassing response to allies of owner faction (and possibly just enforcers)
                        actor.triggerScript("on_detect_target_trespassing", target);
                        actor.triggerBark("on_detect_target_trespassing", target);
                        // TODO - Make actor follow trespasser until they leave the area?
                        detectedActors.get(target).state = DetectionState.TRESPASSING;
                    } else if (actor.getFaction().getRelationTo(target.getFaction().getID()) == Faction.FactionRelation.HOSTILE) {
                        actor.triggerScript("on_detect_target_hostile", target);
                        actor.triggerBark("on_detect_target_hostile", target);
                        detectedActors.get(target).state = DetectionState.HOSTILE;
                    } else {
                        detectedActors.get(target).state = DetectionState.PASSIVE;
                    }
                }
                case TRESPASSING -> {
                    actor.triggerScript("on_trespassing_become_hostile", target);
                    actor.triggerBark("on_trespassing_become_hostile", target);
                    detectedActors.get(target).state = DetectionState.HOSTILE;
                }
            }
        }
    }

    public void addCombatant(Actor target) {
        if (!hasTargetsOfType(DetectionState.HOSTILE)) {
            actor.triggerScript("on_combat_start", target);
            actor.triggerBark("on_combat_start", target);
        }
        if (detectedActors.containsKey(target)) {
            detectedActors.get(target).state = DetectionState.HOSTILE;
            detectedActors.get(target).stateCounter = 0;
            detectedActors.get(target).lastKnownArea = target.getArea();
        } else {
            detectedActors.put(target, new DetectedActor(DetectionState.HOSTILE, target.getArea()));
        }
    }

    public boolean isTargetOfType(Actor target, DetectionState type) {
        return detectedActors.containsKey(target) && detectedActors.get(target).state == type;
    }

    public boolean hasTargetsOfType(DetectionState type) {
        for (DetectedActor actor : detectedActors.values()) {
            if (actor.state == type) {
                return true;
            }
        }
        return false;
    }

    public boolean hasTargetsOfTypeInArea(DetectionState type, Area area) {
        for (DetectedActor actor : detectedActors.values()) {
            if (actor.state == type && actor.lastKnownArea != null && actor.lastKnownArea.equals(area)) {
                return true;
            }
        }
        return false;
    }

    public Set<Actor> getTargetsOfType(DetectionState type) {
        Set<Actor> combatants = new HashSet<>();
        for (Map.Entry<Actor, DetectedActor> actor : detectedActors.entrySet()) {
            if (actor.getValue().state == type) {
                combatants.add(actor.getKey());
            }
        }
        return combatants;
    }

    public Area getLastKnownArea(Actor target) {
        if (!detectedActors.containsKey(target)) {
            return null;
        }
        return detectedActors.get(target).lastKnownArea;
    }

    public float getActionDetectionChance(Action action, Actor subject) {
        Context context = new Context(actor.game(), actor, subject);
        return switch (action.detectionChance()) {
            case LOW -> MathUtils.chanceLinearSkillInverted(subject, Actor.Skill.STEALTH, 0.01f, 0.50f, context);
            case HIGH -> MathUtils.chanceLinearSkillInverted(subject, Actor.Skill.STEALTH, 0.05f, 0.95f, context);
            default -> 0.0f;
        };
    }

    public float getPassiveDetectionChance(Actor subject) {
        AreaLink.DistanceCategory distance = actor.getArea().getDistanceTo(subject.getArea().getID());
        if (subject.isDead()) {
            return switch (distance) {
                case NEAR -> 0.95f;
                case CLOSE -> 0.80f;
                case FAR -> 0.60f;
                case DISTANT ->  0.25f;
            };
        } else {
            Context context = new Context(actor.game(), actor, subject);
            return switch (distance) {
                case NEAR -> MathUtils.chanceLinearSkillInverted(subject, Actor.Skill.STEALTH, 0.50f, 0.95f, context);
                case CLOSE -> MathUtils.chanceLinearSkillInverted(subject, Actor.Skill.STEALTH, 0.20f, 0.80f, context);
                case FAR -> MathUtils.chanceLinearSkillInverted(subject, Actor.Skill.STEALTH, 0.01f, 0.60f, context);
                case DISTANT ->  MathUtils.chanceLinearSkillInverted(subject, Actor.Skill.STEALTH, 0.01f, 0.25f, context);
            };
        }
    }

    public int getStateTriggerValue(DetectionState state) {
        switch (state) {
            case DETECTING -> {
                return alertState.turnsToDetect;
            }
            case TRESPASSING -> {
                return TRESPASSING_TURNS_UNTIL_HOSTILE;
            }
            case HOSTILE, PASSIVE, DEAD, default -> {
                return -1;
            }
        }
    }

    private Set<AreaLink.DistanceCategory> idealDistances() {
        // TODO - Generalize function (not specific to weapon)
        Set<ItemWeapon> equippedWeapons = actor.getEquipmentComponent().getEquippedWeapons();
        if (equippedWeapons.isEmpty()) {
            return Set.of(AreaLink.DistanceCategory.NEAR);
        }
        Set<AreaLink.DistanceCategory> combinedRanges = new HashSet<>();
        for (ItemWeapon weapon : equippedWeapons) {
            combinedRanges.addAll(weapon.getRanges(new Context(actor.game(), actor, actor, weapon)));
        }
        return combinedRanges;
    }

    private Set<Area> idealAreas(Area origin) {
        Set<AreaLink.DistanceCategory> idealDistances = idealDistances();
        return origin.visibleAreasInRange(actor, idealDistances);
    }

    /*public void loadState(SaveData data) {
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
    }*/

    public static class DetectedActor {
        public DetectionState state;
        public int lostVisualCounter; // Counts up each turn while target is not visible, set to 0 when target is visible
        public int stateCounter; // Counts up each turn (or on detection event) while target is visible until reaching state trigger value, used to trigger state changes
        public Area lastKnownArea;
        public AreaTarget areaTarget;

        public DetectedActor(DetectionState state, Area lastKnownArea) {
            this.state = state;
            this.lastKnownArea = lastKnownArea;
            this.lostVisualCounter = 0;
            this.stateCounter = 0;
        }
    }

}
