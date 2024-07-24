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
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.textgen.MultiNoun;
import com.github.finley243.adventureengine.textgen.Noun;
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
    private final int ammoConsumed;
    private final int actionPoints;
    private final WeaponAttackType.WeaponConsumeType weaponConsumeType;
    private final Set<AreaLink.DistanceCategory> ranges;
    private final int rate;
    private final Script damage;
    private final String damageType;
    private final float armorMult;
    private final List<String> targetEffects;
    private final Script hitChanceExpression;
    private final Script hitChanceOverallExpression;
    private final float hitChanceMult;
    private final boolean isLoud;

    public ActionAttack(WeaponAttackType attackType, Item weapon, Set<AttackTarget> targets, Limb limb, Area area, String prompt, String attackPhrase, String attackOverallPhrase, String attackPhraseAudible, String attackOverallPhraseAudible, int ammoConsumed, int actionPoints, WeaponAttackType.WeaponConsumeType weaponConsumeType, Set<AreaLink.DistanceCategory> ranges, int rate, Script damage, String damageType, float armorMult, List<String> targetEffects, Script hitChanceExpression, Script hitChanceOverallExpression, float hitChanceMult, boolean isLoud) {
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
        this.ammoConsumed = ammoConsumed;
        this.actionPoints = actionPoints;
        this.weaponConsumeType = weaponConsumeType;
        this.ranges = ranges;
        this.rate = rate;
        this.damage = damage;
        this.damageType = damageType;
        this.armorMult = armorMult;
        this.targetEffects = targetEffects;
        this.hitChanceExpression = hitChanceExpression;
        this.hitChanceOverallExpression = hitChanceOverallExpression;
        this.hitChanceMult = hitChanceMult;
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

    protected int computeDamage(Context context) {
        Script.ScriptReturnData damageReturn = damage.execute(context);
        if (damageReturn.error() != null) {
            throw new RuntimeException("Error while computing attack damage: " + damageReturn.stackTrace());
        } else if (damageReturn.flowStatement() != null) {
            throw new RuntimeException("Unexpected flow statement in attack damage expression");
        } else if (damageReturn.value() == null) {
            throw new RuntimeException("Attack damage expression returned null");
        } else if (damageReturn.value().getDataType() != Expression.DataType.INTEGER) {
            throw new RuntimeException("Attack damage expression returned non-integer value");
        } else {
            return damageReturn.value().getValueInteger();
        }
    }

    public float hitChanceMult() {
        return hitChanceMult;
    }

    public int getAmmoConsumed() {
        return ammoConsumed;
    }

    public Set<AreaLink.DistanceCategory> getRanges() {
        return ranges;
    }

    public WeaponAttackType.WeaponConsumeType getWeaponConsumeType() {
        return weaponConsumeType;
    }

    @Override
    public float chance(Actor subject, AttackTarget target) {
        if (hitChanceExpression == null) {
            return 1.0f;
        }
        return CombatHelper.calculateHitChance(subject, weapon, target, getLimb(), hitChanceExpression, hitChanceMult());
    }

    @Override
    public float chanceOverall(Actor subject) {
        if (hitChanceOverallExpression == null) {
            return 1.0f;
        }
        return CombatHelper.calculateHitChanceNoTarget(subject, weapon, getLimb(), hitChanceOverallExpression, hitChanceMult());
    }

    @Override
    public boolean onStart(Actor subject, int repeatActionCount) {
        for (AttackTarget target : targets) {
            subject.triggerScript("on_attack", new Context(subject.game(), subject, target));
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
        Context context = new Context(subject.game(), subject, target, getWeapon(), getArea());
        int damage = computeDamage(context);
        context.setLocalVariable("limb", Expression.constant(getLimb() == null ? "null" : getLimb().getName()));
        context.setLocalVariable("relativeTo", Expression.constant(getArea() == null ? "null" : getArea().getRelativeName()));
        context.setLocalVariable("repeats", Expression.constant(repeatActionCount));
        context.setLocalVariable("success", Expression.constant(true));
        context.setLocalVariable("damage", Expression.constant(damage));
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
    public void onSuccessOverall(Actor subject, int repeatActionCount, List<AttackTarget> targetsSuccess, List<AttackTarget> targetsFail) {
        Context context = new Context(subject.game(), subject, null, getWeapon(), getArea());
        context.setLocalVariable("limb", Expression.constant(getLimb() == null ? "null" : getLimb().getName()));
        context.setLocalVariable("relativeTo", Expression.constant(getArea() == null ? "null" : getArea().getRelativeName()));
        context.setLocalVariable("repeats", Expression.constant(repeatActionCount));
        context.setLocalVariable("success", Expression.constant(true));
        List<Noun> targetsSuccessNouns = targetsSuccess.stream().map(target -> (Noun) target).toList();
        List<Noun> targetsFailNouns = targetsFail.stream().map(target -> (Noun) target).toList();
        context.setLocalVariable("targetsSuccess", targetsSuccessNouns.isEmpty() ? null : Expression.constantNoun(new MultiNoun(targetsSuccessNouns)));
        context.setLocalVariable("targetsFail", targetsFailNouns.isEmpty() ? null : Expression.constantNoun(new MultiNoun(targetsFailNouns)));
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
