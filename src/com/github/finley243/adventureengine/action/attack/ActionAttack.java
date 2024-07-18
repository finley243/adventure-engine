package com.github.finley243.adventureengine.action.attack;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionRandomEach;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Limb;
import com.github.finley243.adventureengine.actor.component.TargetingComponent;
import com.github.finley243.adventureengine.combat.CombatHelper;
import com.github.finley243.adventureengine.combat.Damage;
import com.github.finley243.adventureengine.combat.WeaponAttackType;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.AttackTarget;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.environment.AreaLink;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public abstract class ActionAttack extends ActionRandomEach<AttackTarget> {

    public static final boolean BLOCK_ALL_ATTACKS_BEYOND_RATE_LIMIT = true;
    public static final boolean REPEATS_USE_NO_ACTION_POINTS = true;

    public enum AttackHitChanceType {
        INDEPENDENT, JOINT
    }

    private final WeaponAttackType attackType;
    private final Set<AttackTarget> targets;
    private final Item weapon;
    private final Limb limb;
    private final Area area;
    private final String prompt;
    private final String attackPhrase;
    private final String attackOverallPhrase;
    private final String attackPhraseAudible;
    private final String attackOverallPhraseAudible;
    private final String attackSkill;
    private final float baseHitChanceMin;
    private final float baseHitChanceMax;
    private final int ammoConsumed;
    private final int actionPoints;
    private final WeaponAttackType.WeaponConsumeType weaponConsumeType;
    private final Set<AreaLink.DistanceCategory> ranges;
    private final int rate;
    private final int damage;
    private final String damageType;
    private final float armorMult;
    private final List<String> targetEffects;
    private final float hitChanceMult;
    private final String dodgeSkill;
    private final AttackHitChanceType hitChanceType;
    private final boolean isLoud;

    public ActionAttack(WeaponAttackType attackType, Item weapon, Set<AttackTarget> targets, Limb limb, Area area, String prompt, String attackPhrase, String attackOverallPhrase, String attackPhraseAudible, String attackOverallPhraseAudible, String attackSkill, float baseHitChanceMin, float baseHitChanceMax, int ammoConsumed, int actionPoints, WeaponAttackType.WeaponConsumeType weaponConsumeType, Set<AreaLink.DistanceCategory> ranges, int rate, int damage, String damageType, float armorMult, List<String> targetEffects, float hitChanceMult, String dodgeSkill, AttackHitChanceType hitChanceType, boolean isLoud) {
        super(targets);
        this.attackType = attackType;
        this.weapon = weapon;
        this.targets = targets;
        this.limb = limb;
        this.area = area;
        this.prompt = prompt;
        this.attackPhrase = attackPhrase;
        this.attackOverallPhrase = attackOverallPhrase;
        this.attackPhraseAudible = attackPhraseAudible;
        this.attackOverallPhraseAudible = attackOverallPhraseAudible;
        this.attackSkill = attackSkill;
        this.baseHitChanceMin = baseHitChanceMin;
        this.baseHitChanceMax = baseHitChanceMax;
        this.ammoConsumed = ammoConsumed;
        this.actionPoints = actionPoints;
        this.weaponConsumeType = weaponConsumeType;
        this.ranges = ranges;
        this.rate = rate;
        this.damage = damage;
        this.damageType = damageType;
        this.armorMult = armorMult;
        this.targetEffects = targetEffects;
        this.hitChanceMult = hitChanceMult;
        this.dodgeSkill = dodgeSkill;
        this.hitChanceType = hitChanceType;
        this.isLoud = isLoud;
    }

    @Override
    public String getID() {
        return "attack_" + attackType.getID();
    }

    @Override
    public Context getContext(Actor subject) {
        Context context = new Context(subject.game(), subject, targets.size() == 1 ? targets.iterator().next() : null, getWeapon(), getArea());
        context.setLocalVariable("targets", Expression.constant(targets));
        return context;
    }

    public abstract void consumeAmmo(Actor subject);

    public String getAttackTypeID() {
        return attackType.getID();
    }

    @Override
    public String getPrompt(Actor subject) {
        return prompt;
    }

    public Set<AttackTarget> getTargets() {
        return targets;
    }

    public Item getWeapon() {
        return weapon;
    }

    public Limb getLimb() {
        return limb;
    }

    public Area getArea() {
        return area;
    }

    public int damage() {
        return damage;
    }

    public float hitChanceMult() {
        return hitChanceMult;
    }

    public String getDodgeSkill() {
        return dodgeSkill;
    }

    public int getAmmoConsumed() {
        return ammoConsumed;
    }

    public String getAttackSkill() {
        return attackSkill;
    }

    public Set<AreaLink.DistanceCategory> getRanges() {
        return ranges;
    }

    public WeaponAttackType.WeaponConsumeType getWeaponConsumeType() {
        return weaponConsumeType;
    }

    @Override
    public float chance(Actor subject, AttackTarget target) {
        if (hitChanceType == AttackHitChanceType.INDEPENDENT) {
            return CombatHelper.calculateHitChance(subject, weapon, target, getLimb(), getAttackSkill(), getDodgeSkill(), baseHitChanceMin, baseHitChanceMax, hitChanceMult());
        } else {
            if (getDodgeSkill() != null) {
                return CombatHelper.calculateHitChanceDodgeOnly(subject, target, getAttackSkill(), getDodgeSkill());
            } else {
                return 1.0f;
            }
        }
    }

    @Override
    public float chanceOverall(Actor subject) {
        if (hitChanceType == AttackHitChanceType.INDEPENDENT) {
            return 1.0f;
        } else {
            return CombatHelper.calculateHitChanceNoTarget(subject, weapon, getLimb(), getAttackSkill(), baseHitChanceMin, baseHitChanceMax, hitChanceMult());
        }
    }

    @Override
    public boolean onStart(Actor subject, int repeatActionCount) {
        for (AttackTarget target : targets) {
            if (target instanceof Actor) {
                subject.triggerScript("on_attack", new Context(subject.game(), subject, target));
            } else {
                subject.triggerScript("on_attack", new Context(subject.game(), subject, target));
            }
        }
        consumeAmmo(subject);
        return true;
    }

    @Override
    public void onEnd(Actor subject, int repeatActionCount) {
        // TODO - Use illegal action system to add targets if detected
        for (AttackTarget target : targets) {
            if (target instanceof Actor && ((Actor) target).getTargetingComponent() != null) {
                ((Actor) target).getTargetingComponent().addCombatant(subject);
            }
        }
    }

    @Override
    public void onSuccess(Actor subject, AttackTarget target, int repeatActionCount) {
        int damage = damage();
        Context context = new Context(subject.game(), subject, target, getWeapon(), getArea());
        context.setLocalVariable("limb", Expression.constant(getLimb() == null ? "null" : getLimb().getName()));
        context.setLocalVariable("relativeTo", Expression.constant(getArea() == null ? "null" : getArea().getRelativeName()));
        context.setLocalVariable("repeats", Expression.constant(repeatActionCount));
        context.setLocalVariable("success", Expression.constant(true));
        SensoryEvent.execute(subject.game(), new SensoryEvent(subject.getArea(), Phrases.get(attackPhrase), Phrases.get(attackPhraseAudible), context, true, isLoud, null, null));
        Damage damageData = new Damage(damageType, damage, getLimb(), armorMult, targetEffects);
        target.damage(damageData, context);
        subject.triggerScript("on_attack_success", context);
    }

    @Override
    public void onFail(Actor subject, AttackTarget target, int repeatActionCount) {
        Context context = new Context(subject.game(), subject, target, getWeapon(), getArea());
        context.setLocalVariable("limb", Expression.constant(getLimb() == null ? "null" : getLimb().getName()));
        context.setLocalVariable("relativeTo", Expression.constant(getArea() == null ? "null" : getArea().getRelativeName()));
        context.setLocalVariable("repeats", Expression.constant(repeatActionCount));
        context.setLocalVariable("success", Expression.constant(false));
        SensoryEvent.execute(subject.game(), new SensoryEvent(subject.getArea(), Phrases.get(attackPhrase), Phrases.get(attackPhraseAudible), context, true, isLoud, null, null));
        subject.triggerScript("on_attack_failure", context);
    }

    @Override
    public void onSuccessOverall(Actor subject, int repeatActionCount) {
        Context context = new Context(subject.game(), subject, null, getWeapon(), getArea());
        context.setLocalVariable("limb", Expression.constant(getLimb() == null ? "null" : getLimb().getName()));
        context.setLocalVariable("relativeTo", Expression.constant(getArea() == null ? "null" : getArea().getRelativeName()));
        context.setLocalVariable("repeats", Expression.constant(repeatActionCount));
        context.setLocalVariable("success", Expression.constant(true));
        SensoryEvent.execute(subject.game(), new SensoryEvent(subject.getArea(), Phrases.get(attackOverallPhrase), Phrases.get(attackOverallPhraseAudible), context, true, isLoud, this, null));
    }

    @Override
    public void onFailOverall(Actor subject, int repeatActionCount) {
        Context context = new Context(subject.game(), subject, null, getWeapon(), getArea());
        context.setLocalVariable("limb", Expression.constant(getLimb() == null ? "null" : getLimb().getName()));
        context.setLocalVariable("relativeTo", Expression.constant(getArea() == null ? "null" : getArea().getRelativeName()));
        context.setLocalVariable("repeats", Expression.constant(repeatActionCount));
        context.setLocalVariable("success", Expression.constant(false));
        SensoryEvent.execute(subject.game(), new SensoryEvent(subject.getArea(), Phrases.get(attackOverallPhrase), Phrases.get(attackOverallPhraseAudible), context, true, isLoud, this, null));
    }

    @Override
    public int repeatCount(Actor subject) {
        return rate;
    }

    @Override
    public boolean isRepeatMatch(Action action) {
        return action instanceof ActionAttack actionAttack && Objects.equals(actionAttack.getWeapon(), this.getWeapon()) && actionAttack.attackType.equals(this.attackType);
    }

    @Override
    public boolean repeatsUseNoActionPoints() {
        return REPEATS_USE_NO_ACTION_POINTS;
    }

    @Override
    public int actionPoints(Actor subject) {
        return actionPoints;
    }

    @Override
    public float utility(Actor subject) {
        if (subject.getTargetingComponent() == null) return 0.0f;
        for (AttackTarget target : targets) {
            if (target instanceof Actor && subject.getTargetingComponent().isTargetOfType((Actor) target, TargetingComponent.DetectionState.HOSTILE)) {
                return 0.8f;
            }
        }
        return 0.0f;
    }

    @Override
    public boolean isBlockedMatch(Action action) {
        if (BLOCK_ALL_ATTACKS_BEYOND_RATE_LIMIT) {
            return action instanceof ActionAttack;
        }
        return action instanceof ActionAttack actionAttack && Objects.equals(actionAttack.getWeapon(), this.getWeapon()) && actionAttack.attackType.equals(this.attackType);
    }

    @Override
    public ActionDetectionChance detectionChance() {
        return ActionDetectionChance.HIGH;
    }

}
