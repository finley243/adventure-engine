package com.github.finley243.adventureengine.actor.component;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.MathUtils;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.attack.ActionAttack;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Bark;
import com.github.finley243.adventureengine.actor.Faction;
import com.github.finley243.adventureengine.actor.ai.AreaTarget;
import com.github.finley243.adventureengine.actor.ai.UtilityUtils;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.component.ItemComponentWeapon;
import com.github.finley243.adventureengine.world.AttackTarget;
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
    private final Context defaultContext;
    private final Map<Actor, DetectedActor> detectedActors;
    private AlertState alertState;

    public TargetingComponent(Actor actor) {
        this.actor = actor;
        this.defaultContext = Context.builder().subject(actor).build();
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
        Context context = Context.from(defaultContext).build();
        if (startedInCombat && !hasTargetsOfType(DetectionState.HOSTILE)) {
            actor.triggerScript("on_combat_end", context);
            actor.triggerBark("on_combat_end", game, context);
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
                combatant.areaTarget.setTargetAreas(idealAreas(game, combatant.lastKnownArea));
            }
        }
    }

    public void onVisibleAction(Action action, Actor subject) {
        float actionDetectionChance = getActionDetectionChance(action, subject);
        processDetectionEvent(subject, actionDetectionChance);
        updateTargetArea(subject, subject.getArea());
        if (isTargetDetected(subject)) {
            if (action instanceof ActionAttack attackAction) {
                if (subject.getFaction().getRelationTo(actor.getFaction().getID()) == Faction.FactionRelation.ALLY) {
                    // If attacker is an ally, add their attack targets as combatants (as long as they're not allies)
                    for (AttackTarget attackTarget : attackAction.getTargets()) {
                        if (attackTarget instanceof Actor targetActor && targetActor.getFaction().getRelationTo(actor.getFaction().getID()) != Faction.FactionRelation.ALLY) {
                            addCombatant(targetActor);
                        }
                    }
                } else {
                    // If any attack target is an ally, add the attacker as a combatant (as long as they're not an ally)
                    for (AttackTarget attackTarget : attackAction.getTargets()) {
                        if (attackTarget instanceof Actor targetActor && targetActor.getFaction().getRelationTo(actor.getFaction().getID()) == Faction.FactionRelation.ALLY) {
                            addCombatant(subject);
                            break;
                        }
                    }
                }
            }
        }
        // TODO - Handle criminal action detection
    }

    public void onAudibleBark(Bark bark, Actor subject, Actor target, boolean visible) {
        if (bark.responseType() == Bark.BarkResponseType.HOSTILE && subject.getFaction().getRelationTo(actor.getFaction().getID()) == Faction.FactionRelation.ALLY) {
            addCombatant(target);
        }
    }

    private void processDetectionEvent(Actor target, float detectionChance) {
        Context context = Context.from(defaultContext).target(target).build();
        if (!detectedActors.containsKey(target) || detectedActors.get(target).state == DetectionState.DETECTING) {
            boolean detected = MathUtils.randomCheck(detectionChance);
            if (detected) {
                if (!detectedActors.containsKey(target)) {
                    detectedActors.put(target, new DetectedActor(DetectionState.DETECTING, target.getArea()));
                }
                int eventsUntilDetected = getStateTriggerValue(DetectionState.DETECTING) - detectedActors.get(target).stateCounter;
                context.setLocalVariable("detectionRemaining", Expression.constant(eventsUntilDetected));
                if (eventsUntilDetected <= DEDICATED_DETECTION_BARKS) {
                    for (int i = 1; i <= DEDICATED_DETECTION_BARKS; i++) {
                        actor.triggerBark("on_update_detection_" + i, context);
                    }
                } else {
                    actor.triggerBark("on_update_detection", context);
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
            actor.triggerScript("on_target_trespassing_start", context);
            actor.triggerBark("on_target_trespassing_start", game, context);
        } else if (detectedActors.get(target).state == DetectionState.TRESPASSING && !actorIsTrespassing(target)) {
            detectedActors.get(target).state = DetectionState.PASSIVE;
            detectedActors.get(target).stateCounter = 0;
            actor.triggerScript("on_target_trespassing_end", context);
            actor.triggerBark("on_target_trespassing_end", context);
        }
    }

    private void updateState(Actor target) {
        Context context = Context.from(defaultContext).target(target).build();
        detectedActors.get(target).stateCounter += 1;
        if (detectedActors.get(target).stateCounter >= getStateTriggerValue(detectedActors.get(target).state)) {
            detectedActors.get(target).stateCounter = 0;
            switch (detectedActors.get(target).state) {
                case DETECTING -> {
                    boolean isTrespassing = actorIsTrespassing(target);
                    if (actor.isDead()) {
                        // TODO - Limit response to actors in allied faction?
                        actor.triggerScript("on_detect_dead", context);
                        actor.triggerBark("on_detect_dead", context);
                        detectedActors.get(target).state = DetectionState.DEAD;
                    } else if (isTrespassing && target.getArea().getRestrictionType() == Area.RestrictionType.PRIVATE) {
                        // TODO - Limit trespassing response to allies of owner faction (and possibly just enforcers)
                        actor.triggerScript("on_detect_target_trespassing", context);
                        actor.triggerBark("on_detect_target_trespassing", context);
                        // TODO - Make actor follow trespasser until they leave the area?
                        detectedActors.get(target).state = DetectionState.TRESPASSING;
                    } else if (isTrespassing && target.getArea().getRestrictionType() == Area.RestrictionType.HOSTILE) {
                        actor.triggerScript("on_detect_target_hostile_area", context);
                        actor.triggerBark("on_detect_target_hostile_area", context);
                        detectedActors.get(target).state = DetectionState.HOSTILE;
                    } else if (actor.getFaction().getRelationTo(target.getFaction().getID()) == Faction.FactionRelation.HOSTILE) {
                        actor.triggerScript("on_detect_target_hostile_faction", context);
                        actor.triggerBark("on_detect_target_hostile_faction", context);
                        detectedActors.get(target).state = DetectionState.HOSTILE;
                    } else {
                        actor.triggerScript("on_detect_target_passive", context);
                        actor.triggerBark("on_detect_target_passive", context);
                        detectedActors.get(target).state = DetectionState.PASSIVE;
                    }
                }
                case TRESPASSING -> {
                    actor.triggerScript("on_trespassing_become_hostile", context);
                    actor.triggerBark("on_trespassing_become_hostile", context);
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
        Faction areaFaction = area.getOwnerFaction();
        if (target.getFaction().equals(areaFaction)) {
            return false;
        }
        return !area.allowAllies() || areaFaction.getRelationTo(target.getFaction().getID()) != Faction.FactionRelation.ALLY;
    }

    public void addCombatant(Game game, Actor target) {
        Context context = Context.from(defaultContext).target(target).build();
        if (!hasTargetsOfType(DetectionState.HOSTILE)) {
            actor.triggerScript("on_combat_start", context);
            actor.triggerBark("on_combat_start", game, context);
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

    public boolean isTargetDetected(Actor target) {
        return detectedActors.containsKey(target) && detectedActors.get(target).state != DetectionState.DETECTING;
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
        Context context = Context.from(defaultContext).target(subject).build();
        // TODO - Allow specifying detection skill
        return switch (action.detectionChance()) {
            case LOW -> MathUtils.chanceLinearSkillInverted(subject, "stealth", 0.01f, 0.50f, context);
            case HIGH -> MathUtils.chanceLinearSkillInverted(subject, "stealth", 0.05f, 0.95f, context);
            default -> 0.0f;
        };
    }

    public float getPassiveDetectionChance(Game game, Actor subject) {
        if (!subject.isSneaking()) {
            return 1.0f;
        }
        AreaLink.DistanceCategory distance = actor.getArea().getLinearDistanceTo(game, subject.getArea());
        if (subject.isDead()) {
            return switch (distance) {
                case NEAR -> 0.95f;
                case CLOSE -> 0.80f;
                case FAR -> 0.60f;
                case DISTANT ->  0.25f;
            };
        } else {
            Context context = Context.from(defaultContext).target(subject).build();
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
            combinedRanges.addAll(weapon.getComponentOfType(ItemComponentWeapon.class).getRanges(Context.from(defaultContext).parentItem(weapon).build()));
        }
        return combinedRanges;
    }

    private Set<Area> idealAreas(Game game, Area targetArea) {
        Set<AreaLink.DistanceCategory> idealDistances = idealDistances();
        return targetArea.visibleAreasInRange(game, actor, idealDistances);
    }

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
