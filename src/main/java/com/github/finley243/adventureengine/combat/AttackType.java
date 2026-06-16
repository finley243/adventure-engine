package com.github.finley243.adventureengine.combat;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.attack.ActionAttackArea;
import com.github.finley243.adventureengine.action.attack.ActionAttackBasic;
import com.github.finley243.adventureengine.action.attack.ActionAttackLimb;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Limb;
import com.github.finley243.adventureengine.actor.ai.Pathfinder;
import com.github.finley243.adventureengine.effect.Effect;
import com.github.finley243.adventureengine.event.SensoryEventDispatcher;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.component.WeaponItemComponent;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.script.ScriptRuntime;
import com.github.finley243.adventureengine.world.AttackTarget;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.environment.AreaLink;

import java.util.*;

public class AttackType {

    public enum AttackCategory {
        SINGLE, TARGETED, SPREAD
    }

    public enum WeaponConsumeType {
        NONE, PLACE, DESTROY
    }

    private final Pathfinder pathfinder;

    private final String ID;
    private final AttackCategory category;
    private final String prompt;
    private final String attackPhrase;
    private final String attackOverallPhrase;
    private final String attackPhraseAudible;
    private final String attackOverallPhraseAudible;
    private final int ammoConsumed;
    private final int actionPoints;
    private final WeaponConsumeType weaponConsumeType;

    private final boolean useNonIdealRange;
    private final Set<AreaLink.DistanceCategory> rangeOverride;
    private final Integer rateOverride;
    private final Script damageOverride;
    private final float damageMult;
    private final DamageType damageTypeOverride;
    private final Float armorMultOverride;
    private final List<Effect> targetEffects;
    private final boolean overrideTargetEffects;
    private final Script hitChance;
    private final Script hitChanceOverall;
    private final float hitChanceMult;
    private final Boolean isLoudOverride;

    public AttackType(Pathfinder pathfinder, String ID, AttackCategory category, String prompt, String attackPhrase, String attackOverallPhrase, String attackPhraseAudible, String attackOverallPhraseAudible, int ammoConsumed, int actionPoints, WeaponConsumeType weaponConsumeType, boolean useNonIdealRange, Set<AreaLink.DistanceCategory> rangeOverride, Integer rateOverride, Script damageOverride, float damageMult, DamageType damageTypeOverride, Float armorMultOverride, List<Effect> targetEffects, boolean overrideTargetEffects, Script hitChance, Script hitChanceOverall, float hitChanceMult, Boolean isLoudOverride) {
        this.pathfinder = pathfinder;
        this.ID = ID;
        this.category = category;
        this.prompt = prompt;
        this.attackPhrase = attackPhrase;
        this.attackOverallPhrase = attackOverallPhrase;
        this.attackPhraseAudible = attackPhraseAudible;
        this.attackOverallPhraseAudible = attackOverallPhraseAudible;
        this.ammoConsumed = ammoConsumed;
        this.actionPoints = actionPoints;
        this.weaponConsumeType = weaponConsumeType;
        this.useNonIdealRange = useNonIdealRange;
        this.rangeOverride = rangeOverride;
        this.rateOverride = rateOverride;
        this.damageOverride = damageOverride;
        this.damageMult = damageMult;
        this.damageTypeOverride = damageTypeOverride;
        this.armorMultOverride = armorMultOverride;
        this.targetEffects = targetEffects;
        this.overrideTargetEffects = overrideTargetEffects;
        this.hitChance = hitChance;
        this.hitChanceOverall = hitChanceOverall;
        this.hitChanceMult = hitChanceMult;
        this.isLoudOverride = isLoudOverride;
    }

    public String getID() {
        return ID;
    }

    public List<Action> generateActions(Actor subject, Item weapon, ScriptRuntime scriptRuntime, SensoryEventDispatcher sensoryEventDispatcher) {
        if (weapon != null) {
            return generateActionsWeapon(subject, weapon, scriptRuntime, sensoryEventDispatcher);
        } else {
            return generateActionsUnarmed(subject, scriptRuntime, sensoryEventDispatcher);
        }
    }

    private List<Action> generateActionsWeapon(Actor subject, Item weapon, ScriptRuntime scriptRuntime, SensoryEventDispatcher sensoryEventDispatcher) {
        if (weapon == null) throw new IllegalArgumentException("Weapon cannot be null");
        Context context = Context.builder().subject(subject).target(subject).parentItem(weapon).build();
        List<Action> actions = new ArrayList<>();
        Set<AreaLink.DistanceCategory> ranges = rangeOverride != null && !rangeOverride.isEmpty() ? rangeOverride : (useNonIdealRange ? EnumSet.complementOf(EnumSet.copyOf(weapon.getComponentOfType(WeaponItemComponent.class).getRanges(context))) : weapon.getComponentOfType(WeaponItemComponent.class).getRanges(context));
        int rate = rateOverride != null ? rateOverride : weapon.getComponentOfType(WeaponItemComponent.class).getRate(context);
        Script damage = Objects.requireNonNullElseGet(damageOverride, () -> Script.constant((int) (weapon.getComponentOfType(WeaponItemComponent.class).getDamage(context) * (damageMult + 1.0f))));
        DamageType damageType = damageTypeOverride != null ? damageTypeOverride : weapon.getComponentOfType(WeaponItemComponent.class).getDamageType(context);
        float armorMult = armorMultOverride != null ? armorMultOverride : weapon.getComponentOfType(WeaponItemComponent.class).getArmorMult(context);
        boolean isLoud = isLoudOverride != null ? isLoudOverride : weapon.getComponentOfType(WeaponItemComponent.class).isLoud(context);
        List<Effect> targetEffectsCombined = new ArrayList<>(targetEffects);
        if (!overrideTargetEffects) {
            targetEffectsCombined.addAll(weapon.getComponentOfType(WeaponItemComponent.class).getTargetEffects(context));
        }
        switch (category) {
            case SINGLE -> {
                for (Map.Entry<Area, Pathfinder.VisibleAreaData> entry : subject.getVisibleAreas(pathfinder).entrySet()) {
                    AreaLink.DistanceCategory targetDistance = entry.getValue().distance();
                    for (AttackTarget target : entry.getKey().getAttackTargets()) {
                        if (!target.equals(subject) && target.isVisible(subject) && target.canBeAttacked()) {
                            actions.add(new ActionAttackBasic(scriptRuntime, sensoryEventDispatcher, this, weapon, target, prompt, attackPhrase, attackOverallPhrase, attackPhraseAudible, attackOverallPhraseAudible, ammoConsumed, actionPoints, weaponConsumeType, ranges, rate, damage, damageType, armorMult, targetEffectsCombined, hitChance, hitChanceOverall, hitChanceMult, isLoud, targetDistance));
                        }
                    }
                }
            }
            case TARGETED -> {
                for (Map.Entry<Area, Pathfinder.VisibleAreaData> entry : subject.getVisibleAreas(pathfinder).entrySet()) {
                    AreaLink.DistanceCategory targetDistance = entry.getValue().distance();
                    for (Actor target : entry.getKey().getActors()) {
                        if (!target.equals(subject) && target.isVisible(subject) && target.canBeAttacked()) {
                            for (Limb limb : target.getLimbs()) {
                                actions.add(new ActionAttackLimb(scriptRuntime, sensoryEventDispatcher, this, weapon, target, limb, prompt, attackPhrase, attackOverallPhrase, attackPhraseAudible, attackOverallPhraseAudible, ammoConsumed, actionPoints, weaponConsumeType, ranges, rate, damage, damageType, armorMult, targetEffectsCombined, hitChance, hitChanceOverall, hitChanceMult, isLoud, targetDistance));
                            }
                        }
                    }
                }
            }
            case SPREAD -> {
                for (Map.Entry<Area, Pathfinder.VisibleAreaData> entry : subject.getVisibleAreas(pathfinder).entrySet()) {
                    Area target = entry.getKey();
                    AreaLink.DistanceCategory targetDistance = entry.getValue().distance();
                    actions.add(new ActionAttackArea(scriptRuntime, sensoryEventDispatcher, this, weapon, target, prompt, attackPhrase, attackOverallPhrase, attackPhraseAudible, attackOverallPhraseAudible, ammoConsumed, actionPoints, weaponConsumeType, ranges, rate, damage, damageType, armorMult, targetEffectsCombined, hitChance, hitChanceOverall, hitChanceMult, isLoud, targetDistance));
                }
            }
        }
        return actions;
    }

    private List<Action> generateActionsUnarmed(Actor subject, ScriptRuntime scriptRuntime, SensoryEventDispatcher sensoryEventDispatcher) {
        Set<AreaLink.DistanceCategory> ranges = rangeOverride;
        int rate = rateOverride;
        Script damage = damageOverride;
        DamageType damageType = damageTypeOverride;
        float armorMult = armorMultOverride;
        boolean isLoud = isLoudOverride;
        List<Effect> targetEffectsCombined = new ArrayList<>(targetEffects);
        List<Action> actions = new ArrayList<>();
        switch (category) {
            case SINGLE -> {
                for (Map.Entry<Area, Pathfinder.VisibleAreaData> entry : subject.getVisibleAreas(pathfinder).entrySet()) {
                    AreaLink.DistanceCategory targetDistance = entry.getValue().distance();
                    for (AttackTarget target : entry.getKey().getAttackTargets()) {
                        if (!target.equals(subject) && target.isVisible(subject) && target.canBeAttacked()) {
                            actions.add(new ActionAttackBasic(scriptRuntime, sensoryEventDispatcher, this, null, target, prompt, attackPhrase, attackOverallPhrase, attackPhraseAudible, attackOverallPhraseAudible, ammoConsumed, actionPoints, weaponConsumeType, ranges, rate, damage, damageType, armorMult, targetEffectsCombined, hitChance, hitChanceOverall, hitChanceMult, isLoud, targetDistance));
                        }
                    }
                }
            }
            case TARGETED -> {
                for (Map.Entry<Area, Pathfinder.VisibleAreaData> entry : subject.getVisibleAreas(pathfinder).entrySet()) {
                    AreaLink.DistanceCategory targetDistance = entry.getValue().distance();
                    for (Actor target : entry.getKey().getActors()) {
                        if (!target.equals(subject) && target.isVisible(subject) && target.canBeAttacked()) {
                            for (Limb limb : target.getLimbs()) {
                                actions.add(new ActionAttackLimb(scriptRuntime, sensoryEventDispatcher, this, null, target, limb, prompt, attackPhrase, attackOverallPhrase, attackPhraseAudible, attackOverallPhraseAudible, ammoConsumed, actionPoints, weaponConsumeType, ranges, rate, damage, damageType, armorMult, targetEffectsCombined, hitChance, hitChanceOverall, hitChanceMult, isLoud, targetDistance));
                            }
                        }
                    }
                }
            }
            case SPREAD -> {
                for (Map.Entry<Area, Pathfinder.VisibleAreaData> entry : subject.getVisibleAreas(pathfinder).entrySet()) {
                    Area target = entry.getKey();
                    AreaLink.DistanceCategory targetDistance = entry.getValue().distance();
                    actions.add(new ActionAttackArea(scriptRuntime, sensoryEventDispatcher, this, null, target, prompt, attackPhrase, attackOverallPhrase, attackPhraseAudible, attackOverallPhraseAudible, ammoConsumed, actionPoints, weaponConsumeType, ranges, rate, damage, damageType, armorMult, targetEffectsCombined, hitChance, hitChanceOverall, hitChanceMult, isLoud, targetDistance));
                }
            }
        }
        return actions;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof AttackType attackType && getID().equals(attackType.getID());
    }

}
