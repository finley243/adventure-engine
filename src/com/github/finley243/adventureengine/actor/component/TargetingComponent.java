package com.github.finley243.adventureengine.actor.component;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.MapBuilder;
import com.github.finley243.adventureengine.MathUtils;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Faction;
import com.github.finley243.adventureengine.actor.ai.AreaTarget;
import com.github.finley243.adventureengine.actor.ai.UtilityUtils;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.expression.ExpressionConstantInteger;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.component.ItemComponentWeapon;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.environment.AreaLink;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TargetingComponent {

    private static final AlertState DEFAULT_ALERT_STATE = AlertState.AWARE;
    private static final int TRESPASSING_TURNS_UNTIL_HOSTILE = 2;
    private static final int DEDICATED_DETECTION_BARKS = 2;

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
        Set<Actor> lineOfSightActors = actor.getLineOfSightActors();
        detectedActors.entrySet().removeIf(entry -> {
            if (lineOfSightActors.contains(entry.getKey()) && entry.getKey().isVisible(actor)) {
                entry.getValue().lostVisualCounter = 0;
                entry.getValue().lastKnownArea = entry.getKey().getArea();
                if (entry.getValue().state.updateOnTurn) {
                    updateState(entry.getKey());
                }
            } else {
                if (entry.getValue().state.turnsUntilRemove != -1) {
                    entry.getValue().lostVisualCounter += 1;
                    if (entry.getValue().lostVisualCounter >= entry.getValue().state.turnsUntilRemove) {
                        if (entry.getValue().areaTarget != null) {
                            entry.getValue().areaTarget.markForRemoval();
                        }
                        return true;
                    }
                }
            }
            return false;
        });
    }

    public void update() {
        Set<Actor> lineOfSightActors = actor.getLineOfSightActors();
        for (Actor lineOfSightActor : lineOfSightActors) {
            if (lineOfSightActor.isVisible(actor)) {
                processDetectionEvent(lineOfSightActor, getPassiveDetectionChance(lineOfSightActor));
            }
        }
        boolean startedInCombat = false;
        for (Map.Entry<Actor, DetectedActor> entry : detectedActors.entrySet()) {
            if (entry.getValue().state == DetectionState.HOSTILE) {
                startedInCombat = true;
                if (lineOfSightActors.contains(entry.getKey()) && entry.getKey().isVisible(actor)) {
                    entry.getValue().lastKnownArea = entry.getKey().getArea();
                }
                updateTargetHostile(entry.getKey(), entry.getValue());
            }
        }
        if (startedInCombat && !hasTargetsOfType(DetectionState.HOSTILE)) {
            actor.triggerScript("on_combat_end", new Context(actor.game(), actor, actor));
            actor.triggerBark("on_combat_end", new Context(actor.game(), actor, actor));
        }
    }

    private void updateTargetHostile(Actor target, DetectedActor targetData) {
        if (target.isDead()) {
            targetData.state = DetectionState.DEAD;
            targetData.stateCounter = 0;
            if (targetData.areaTarget != null) {
                targetData.areaTarget.markForRemoval();
                targetData.areaTarget = null;
            }
        } else if (targetData.areaTarget == null) {
            targetData.areaTarget = new AreaTarget(idealAreas(targetData.lastKnownArea), UtilityUtils.getPursueTargetUtility(actor, target), true);
            actor.addPursueTarget(targetData.areaTarget);
        } else {
            targetData.areaTarget.setTargetAreas(idealAreas(targetData.lastKnownArea));
            targetData.areaTarget.setTargetUtility(UtilityUtils.getPursueTargetUtility(actor, target));
        }
    }

    public void updateTargetArea(Actor target, Area area) {
        if (detectedActors.containsKey(target)) {
            DetectedActor combatant = detectedActors.get(target);
            combatant.lastKnownArea = area;
            if (combatant.areaTarget != null) {
                combatant.areaTarget.setTargetAreas(idealAreas(combatant.lastKnownArea));
            }
        }
    }

    public void onVisibleAction(Action action, Actor target) {
        float actionDetectionChance = getActionDetectionChance(action, target);
        processDetectionEvent(target, actionDetectionChance);
        updateTargetArea(target, target.getArea());
        // TODO - Handle criminal action detection
    }

    private void processDetectionEvent(Actor target, float detectionChance) {
        if (!detectedActors.containsKey(target) || detectedActors.get(target).state == DetectionState.DETECTING) {
            boolean detected = MathUtils.randomCheck(detectionChance);
            if (detected) {
                if (!detectedActors.containsKey(target)) {
                    detectedActors.put(target, new DetectedActor(DetectionState.DETECTING, target.getArea()));
                }
                int eventsUntilDetected = getStateTriggerValue(DetectionState.DETECTING) - detectedActors.get(target).stateCounter;
                if (eventsUntilDetected <= DEDICATED_DETECTION_BARKS) {
                    for (int i = 1; i <= DEDICATED_DETECTION_BARKS; i++) {
                        actor.triggerBark("on_update_detection_" + i, new Context(actor.game(), actor, target, new MapBuilder<String, Expression>().put("detectionRemaining", new ExpressionConstantInteger(eventsUntilDetected)).build()));
                    }
                } else {
                    actor.triggerBark("on_update_detection", new Context(actor.game(), actor, target, new MapBuilder<String, Expression>().put("detectionRemaining", new ExpressionConstantInteger(eventsUntilDetected)).build()));
                }
                updateState(target);
            }
        } else if (detectedActors.get(target).state == DetectionState.PASSIVE && actorIsTrespassing(target)) {
            if (target.getArea().getRestrictionType() == Area.RestrictionType.HOSTILE) {
                detectedActors.get(target).state = DetectionState.HOSTILE;
            } else {
                detectedActors.get(target).state = DetectionState.TRESPASSING;
            }
            detectedActors.get(target).stateCounter = 0;
            actor.triggerScript("on_target_trespassing_start", new Context(actor.game(), actor, target));
            actor.triggerBark("on_target_trespassing_start", new Context(actor.game(), actor, target));
        } else if (detectedActors.get(target).state == DetectionState.TRESPASSING && !actorIsTrespassing(target)) {
            detectedActors.get(target).state = DetectionState.PASSIVE;
            detectedActors.get(target).stateCounter = 0;
            actor.triggerScript("on_target_trespassing_end", new Context(actor.game(), actor, target));
            actor.triggerBark("on_target_trespassing_end", new Context(actor.game(), actor, target));
        }
    }

    private void updateState(Actor target) {
        detectedActors.get(target).stateCounter += 1;
        if (detectedActors.get(target).stateCounter >= getStateTriggerValue(detectedActors.get(target).state)) {
            detectedActors.get(target).stateCounter = 0;
            switch (detectedActors.get(target).state) {
                case DETECTING -> {
                    boolean isTrespassing = actorIsTrespassing(target);
                    if (actor.isDead()) {
                        // TODO - Limit response to actors in allied faction?
                        actor.triggerScript("on_detect_dead", new Context(actor.game(), actor, target));
                        actor.triggerBark("on_detect_dead", new Context(actor.game(), actor, target));
                        detectedActors.get(target).state = DetectionState.DEAD;
                    } else if (isTrespassing && target.getArea().getRestrictionType() == Area.RestrictionType.PRIVATE) {
                        // TODO - Limit trespassing response to allies of owner faction (and possibly just enforcers)
                        actor.triggerScript("on_detect_target_trespassing", new Context(actor.game(), actor, target));
                        actor.triggerBark("on_detect_target_trespassing", new Context(actor.game(), actor, target));
                        // TODO - Make actor follow trespasser until they leave the area?
                        detectedActors.get(target).state = DetectionState.TRESPASSING;
                    } else if (isTrespassing && target.getArea().getRestrictionType() == Area.RestrictionType.HOSTILE) {
                        actor.triggerScript("on_detect_target_hostile_area", new Context(actor.game(), actor, target));
                        actor.triggerBark("on_detect_target_hostile_area", new Context(actor.game(), actor, target));
                        detectedActors.get(target).state = DetectionState.HOSTILE;
                    } else if (actor.getFaction().getRelationTo(target.getFaction().getID()) == Faction.FactionRelation.HOSTILE) {
                        actor.triggerScript("on_detect_target_hostile_faction", new Context(actor.game(), actor, target));
                        actor.triggerBark("on_detect_target_hostile_faction", new Context(actor.game(), actor, target));
                        detectedActors.get(target).state = DetectionState.HOSTILE;
                    } else {
                        actor.triggerScript("on_detect_target_passive", new Context(actor.game(), actor, target));
                        actor.triggerBark("on_detect_target_passive", new Context(actor.game(), actor, target));
                        detectedActors.get(target).state = DetectionState.PASSIVE;
                    }
                }
                case TRESPASSING -> {
                    actor.triggerScript("on_trespassing_become_hostile", new Context(actor.game(), actor, target));
                    actor.triggerBark("on_trespassing_become_hostile", new Context(actor.game(), actor, target));
                    detectedActors.get(target).state = DetectionState.HOSTILE;
                }
            }
        }
    }

    private boolean actorIsTrespassing(Actor target) {
        Area area = target.getArea();
        if (area.getRestrictionType() == Area.RestrictionType.PUBLIC || area.getOwnerFaction() == null) {
            return false;
        }
        Faction areaFaction = actor.game().data().getFaction(area.getOwnerFaction());
        if (target.getFaction().equals(areaFaction)) {
            return true;
        }
        return area.allowAllies() && areaFaction.getRelationTo(target.getFaction().getID()) != Faction.FactionRelation.ALLY;
    }

    public void addCombatant(Actor target) {
        if (!hasTargetsOfType(DetectionState.HOSTILE)) {
            actor.triggerScript("on_combat_start", new Context(actor.game(), actor, target));
            actor.triggerBark("on_combat_start", new Context(actor.game(), actor, target));
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

    public DetectionState getTargetType(Actor target) {
        if (!detectedActors.containsKey(target)) {
            return null;
        }
        return detectedActors.get(target).state;
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
        if (!subject.isSneaking()) {
            return 1.0f;
        }
        Context context = new Context(actor.game(), actor, subject);
        // TODO - Allow specifying detection skill
        return switch (action.detectionChance()) {
            case LOW -> MathUtils.chanceLinearSkillInverted(subject, "stealth", 0.01f, 0.50f, context);
            case HIGH -> MathUtils.chanceLinearSkillInverted(subject, "stealth", 0.05f, 0.95f, context);
            default -> 0.0f;
        };
    }

    public float getPassiveDetectionChance(Actor subject) {
        if (!subject.isSneaking()) {
            return 1.0f;
        }
        AreaLink.DistanceCategory distance = actor.getArea().getLinearDistanceTo(subject.getArea());
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
                case NEAR -> MathUtils.chanceLinearSkillInverted(subject, "stealth", 0.50f, 0.95f, context);
                case CLOSE -> MathUtils.chanceLinearSkillInverted(subject, "stealth", 0.20f, 0.80f, context);
                case FAR -> MathUtils.chanceLinearSkillInverted(subject, "stealth", 0.01f, 0.60f, context);
                case DISTANT ->  MathUtils.chanceLinearSkillInverted(subject, "stealth", 0.01f, 0.25f, context);
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
            default -> { // HOSTILE, PASSIVE, DEAD
                return -1;
            }
        }
    }

    private Set<AreaLink.DistanceCategory> idealDistances() {
        // TODO - Generalize function (not specific to weapon)
        Set<Item> equippedWeapons = actor.getEquipmentComponent().getEquippedWeapons();
        if (equippedWeapons.isEmpty()) {
            return Set.of(AreaLink.DistanceCategory.NEAR);
        }
        Set<AreaLink.DistanceCategory> combinedRanges = new HashSet<>();
        for (Item weapon : equippedWeapons) {
            combinedRanges.addAll(weapon.getComponentOfType(ItemComponentWeapon.class).getRanges(new Context(actor.game(), actor, actor, weapon)));
        }
        return combinedRanges;
    }

    private Set<Area> idealAreas(Area targetArea) {
        Set<AreaLink.DistanceCategory> idealDistances = idealDistances();
        return targetArea.visibleAreasInRange(actor, idealDistances);
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
