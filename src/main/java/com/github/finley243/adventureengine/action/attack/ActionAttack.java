package com.github.finley243.adventureengine.action.attack;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionDependencies;
import com.github.finley243.adventureengine.action.ActionRandomEach;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Limb;
import com.github.finley243.adventureengine.actor.component.TargetingComponent;
import com.github.finley243.adventureengine.combat.AttackType;
import com.github.finley243.adventureengine.combat.CombatHelper;
import com.github.finley243.adventureengine.combat.Damage;
import com.github.finley243.adventureengine.combat.DamageType;
import com.github.finley243.adventureengine.effect.Effect;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.script.ScriptRuntime;
import com.github.finley243.adventureengine.script.ScriptValueHolder;
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

    private final AttackType attackType;
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
    private final AttackType.WeaponConsumeType weaponConsumeType;
    private final Set<AreaLink.DistanceCategory> ranges;
    private final int rate;
    private final Script damage;
    private final DamageType damageType;
    private final float armorMult;
    private final List<Effect> targetEffects;
    private final Script hitChanceExpression;
    private final Script hitChanceOverallExpression;
    private final float hitChanceMult;
    private final boolean isLoud;
    private final AreaLink.DistanceCategory targetDistance;

    public ActionAttack(ActionDependencies dependencies, AttackType attackType, Item weapon, Set<AttackTarget> targets, Limb limb, Area area, String prompt, String attackPhrase, String attackOverallPhrase, String attackPhraseAudible, String attackOverallPhraseAudible, int ammoConsumed, int actionPoints, AttackType.WeaponConsumeType weaponConsumeType, Set<AreaLink.DistanceCategory> ranges, int rate, Script damage, DamageType damageType, float armorMult, List<Effect> targetEffects, Script hitChanceExpression, Script hitChanceOverallExpression, float hitChanceMult, boolean isLoud, AreaLink.DistanceCategory targetDistance) {
        super(dependencies, targets);
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
        this.targetDistance = targetDistance;
    }

    @Override
    public String getID() {
        return "attack_" + attackType.getID();
    }

    @Override
    public Context getContext(Actor subject) {
        Context context = Context.builder().subject(subject).attackTarget(targets.size() == 1 ? targets.iterator().next() : null).parentItem(getWeapon()).parentArea(getArea()).build();
        context.setLocalVariable("targets", Expression.set(targets, e -> Expression.valueHolder((ScriptValueHolder) e)));
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

    protected int computeDamage(ScriptRuntime scriptRuntime, Context context) {
        Expression damageExpression = damage.run(scriptRuntime, context);
        if (damageExpression == null) {
            throw new RuntimeException("Attack damage expression returned null");
        } else if (damageExpression.getDataType() != Expression.DataType.INTEGER) {
            throw new RuntimeException("Attack damage expression returned non-integer value");
        } else {
            return damageExpression.getValueInteger();
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

    public AttackType.WeaponConsumeType getWeaponConsumeType() {
        return weaponConsumeType;
    }

    protected AreaLink.DistanceCategory getTargetDistance() {
        return targetDistance;
    }

    @Override
    public float chance(Actor subject, AttackTarget target) {
        if (hitChanceExpression == null) {
            return 1.0f;
        }
        Context scriptContext = Context.builder().subject(subject).attackTarget(target).parentItem(weapon).build();
        return CombatHelper.calculateHitChance(scriptRuntime, scriptContext, weapon, getLimb(), hitChanceExpression, hitChanceMult());
    }

    @Override
    public float chanceOverall(Actor subject) {
        if (hitChanceOverallExpression == null) {
            return 1.0f;
        }
        Context scriptContext = Context.builder().subject(subject).parentItem(weapon).build();
        return CombatHelper.calculateHitChanceNoTarget(scriptRuntime, scriptContext, weapon, getLimb(), hitChanceOverallExpression, hitChanceMult());
    }

    @Override
    public boolean onStart(Actor subject, int repeatActionCount) {
        for (AttackTarget target : targets) {
            if (subject.isPlayer() && target instanceof Noun targetNoun) {
                targetNoun.setKnown();
            }
            subject.triggerScript("on_attack", Context.builder().subject(subject).attackTarget(target).parentItem(getWeapon()).parentArea(getArea()).build());
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
        Context context = Context.builder().subject(subject).attackTarget(target).parentItem(getWeapon()).parentArea(getArea()).build();
        int damage = computeDamage(scriptRuntime, context);
        context.setLocalVariable("limb", Expression.string(getLimb() == null ? "null" : getLimb().getName()));
        context.setLocalVariable("relativeTo", Expression.string(getArea() == null ? "null" : getArea().getRelativeName()));
        context.setLocalVariable("repeats", Expression.integer(repeatActionCount));
        context.setLocalVariable("success", Expression.bool(true));
        context.setLocalVariable("damage", Expression.integer(damage));
        Damage damageData = new Damage(damageType, damage, getLimb(), armorMult, targetEffects);
        AttackTarget.ComputedDamage computedDamage = target.applyEffectsAndComputeDamage(damageData, scriptRuntime, context);
        context.setLocalVariable("finalDamage", Expression.integer(computedDamage.amount()));
        context.setLocalVariable("isKillingBlow", Expression.bool(computedDamage.isKillingBlow()));
        sensoryEventDispatcher.dispatch(new SensoryEvent(subject.getArea(), Phrases.get(attackPhrase), Phrases.get(attackPhraseAudible), context, true, isLoud, null, null));
        target.applyDamage(computedDamage, scriptRuntime, context);
        subject.triggerScript("on_attack_success", context);
    }

    @Override
    public void onFail(Actor subject, AttackTarget target, int repeatActionCount) {
        Context context = Context.builder().subject(subject).attackTarget(target).parentItem(getWeapon()).parentArea(getArea()).build();
        context.setLocalVariable("limb", Expression.string(getLimb() == null ? "null" : getLimb().getName()));
        context.setLocalVariable("relativeTo", Expression.string(getArea() == null ? "null" : getArea().getRelativeName()));
        context.setLocalVariable("repeats", Expression.integer(repeatActionCount));
        context.setLocalVariable("success", Expression.bool(false));
        sensoryEventDispatcher.dispatch(new SensoryEvent(subject.getArea(), Phrases.get(attackPhrase), Phrases.get(attackPhraseAudible), context, true, isLoud, null, null));
        subject.triggerScript("on_attack_failure", context);
    }

    @Override
    public void onSuccessOverall(Actor subject, int repeatActionCount, List<AttackTarget> targetsSuccess, List<AttackTarget> targetsFail) {
        Context context = Context.builder().subject(subject).parentItem(getWeapon()).parentArea(getArea()).parentAction(this).build();
        context.setLocalVariable("limb", Expression.string(getLimb() == null ? "null" : getLimb().getName()));
        context.setLocalVariable("relativeTo", Expression.string(getArea() == null ? "null" : getArea().getRelativeName()));
        context.setLocalVariable("repeats", Expression.integer(repeatActionCount));
        context.setLocalVariable("success", Expression.bool(true));
        List<Noun> targetsSuccessNouns = targetsSuccess.stream().map(target -> (Noun) target).toList();
        List<Noun> targetsFailNouns = targetsFail.stream().map(target -> (Noun) target).toList();
        context.setLocalVariable("targetsSuccess", targetsSuccessNouns.isEmpty() ? null : Expression.noun(new MultiNoun(targetsSuccessNouns)));
        context.setLocalVariable("targetsFail", targetsFailNouns.isEmpty() ? null : Expression.noun(new MultiNoun(targetsFailNouns)));
        sensoryEventDispatcher.dispatch(new SensoryEvent(subject.getArea(), Phrases.get(attackOverallPhrase), Phrases.get(attackOverallPhraseAudible), context, true, isLoud, this, null));
    }

    @Override
    public void onFailOverall(Actor subject, int repeatActionCount) {
        Context context = Context.builder().subject(subject).parentItem(getWeapon()).parentArea(getArea()).parentAction(this).build();
        context.setLocalVariable("limb", Expression.string(getLimb() == null ? "null" : getLimb().getName()));
        context.setLocalVariable("relativeTo", Expression.string(getArea() == null ? "null" : getArea().getRelativeName()));
        context.setLocalVariable("repeats", Expression.integer(repeatActionCount));
        context.setLocalVariable("success", Expression.bool(false));
        sensoryEventDispatcher.dispatch(new SensoryEvent(subject.getArea(), Phrases.get(attackOverallPhrase), Phrases.get(attackOverallPhraseAudible), context, true, isLoud, this, null));
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
