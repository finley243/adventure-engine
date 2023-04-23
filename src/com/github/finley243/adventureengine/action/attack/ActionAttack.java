package com.github.finley243.adventureengine.action.attack;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionRandomEach;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Limb;
import com.github.finley243.adventureengine.actor.component.TargetingComponent;
import com.github.finley243.adventureengine.combat.CombatHelper;
import com.github.finley243.adventureengine.combat.Damage;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.textgen.TextContext;
import com.github.finley243.adventureengine.textgen.Noun;
import com.github.finley243.adventureengine.MapBuilder;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.AttackTarget;
import com.github.finley243.adventureengine.world.environment.AreaLink;

import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class ActionAttack extends ActionRandomEach<AttackTarget> {

    public enum AttackHitChanceType {
        INDEPENDENT, JOINT
    }

    private final Set<AttackTarget> targets;
    private final Noun weaponNoun;
    private final Limb limb;
    private final String prompt;
    private final String hitPhrase;
    private final String hitPhraseRepeat;
    private final String hitOverallPhrase;
    private final String hitOverallPhraseRepeat;
    private final String missPhrase;
    private final String missPhraseRepeat;
    private final String missOverallPhrase;
    private final String missOverallPhraseRepeat;
    private final Actor.Skill attackSkill;
    private final float baseHitChanceMin;
    private final float baseHitChanceMax;
    private final float hitChanceBonus;
    private final int ammoConsumed;
    private final Set<AreaLink.DistanceCategory> ranges;
    private final int rate;
    private final int damage;
    private final Damage.DamageType damageType;
    private final float armorMult;
    private final List<String> targetEffects;
    private final float hitChanceMult;
    private final boolean canDodge;
    private final AttackHitChanceType hitChanceType;

    public ActionAttack(Noun weaponNoun, Set<AttackTarget> targets, Limb limb, String prompt, String hitPhrase, String hitPhraseRepeat, String hitOverallPhrase, String hitOverallPhraseRepeat, String missPhrase, String missPhraseRepeat, String missOverallPhrase, String missOverallPhraseRepeat, Actor.Skill attackSkill, float baseHitChanceMin, float baseHitChanceMax, float hitChanceBonus, int ammoConsumed, Set<AreaLink.DistanceCategory> ranges, int rate, int damage, Damage.DamageType damageType, float armorMult, List<String> targetEffects, float hitChanceMult, boolean canDodge, AttackHitChanceType hitChanceType) {
        super(targets);
        this.weaponNoun = weaponNoun;
        this.targets = targets;
        this.limb = limb;
        this.prompt = prompt;
        this.hitPhrase = hitPhrase;
        this.hitPhraseRepeat = hitPhraseRepeat;
        this.hitOverallPhrase = hitOverallPhrase;
        this.hitOverallPhraseRepeat = hitOverallPhraseRepeat;
        this.missPhrase = missPhrase;
        this.missPhraseRepeat = missPhraseRepeat;
        this.missOverallPhrase = missOverallPhrase;
        this.missOverallPhraseRepeat = missOverallPhraseRepeat;
        this.attackSkill = attackSkill;
        this.baseHitChanceMin = baseHitChanceMin;
        this.baseHitChanceMax = baseHitChanceMax;
        this.hitChanceBonus = hitChanceBonus;
        this.ammoConsumed = ammoConsumed;
        this.ranges = ranges;
        this.rate = rate;
        this.damage = damage;
        this.damageType = damageType;
        this.armorMult = armorMult;
        this.targetEffects = targetEffects;
        this.hitChanceMult = hitChanceMult;
        this.canDodge = canDodge;
        this.hitChanceType = hitChanceType;
    }

    public abstract void consumeAmmo(Actor subject);

    public String getPrompt() {
        return prompt;
    }

    public Set<AttackTarget> getTargets() {
        return targets;
    }

    public Noun getWeaponNoun() {
        return weaponNoun;
    }

    public Limb getLimb() {
        return limb;
    }

    public int damage() {
        return damage;
    }

    public float hitChanceMult() {
        return hitChanceMult;
    }

    public boolean canDodge() {
        return canDodge;
    }

    public int getAmmoConsumed() {
        return ammoConsumed;
    }

    public Actor.Skill getAttackSkill() {
        return attackSkill;
    }

    public Set<AreaLink.DistanceCategory> getRanges() {
        return ranges;
    }

    @Override
    public float chance(Actor subject, AttackTarget target) {
        if (hitChanceType == AttackHitChanceType.INDEPENDENT) {
            return CombatHelper.calculateHitChance(subject, target, getLimb(), getAttackSkill(), Actor.Skill.DODGE, baseHitChanceMin, baseHitChanceMax, hitChanceBonus, canDodge(), hitChanceMult());
        } else {
            if (canDodge()) {
                return CombatHelper.calculateHitChanceDodgeOnly(subject, target, getAttackSkill(), Actor.Skill.DODGE);
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
            return CombatHelper.calculateHitChanceNoTarget(subject, getLimb(), getAttackSkill(), baseHitChanceMin, baseHitChanceMax, hitChanceBonus, hitChanceMult());
        }
    }

    @Override
    public boolean onStart(Actor subject, int repeatActionCount) {
        for (AttackTarget target : targets) {
            if (target instanceof Actor) {
                subject.triggerScript("on_attack", (Actor) target);
            } else {
                subject.triggerScript("on_attack", subject);
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
        TextContext attackContext = new TextContext(Map.of("limb", (getLimb() == null ? "null" : getLimb().getName())), new MapBuilder<String, Noun>().put("actor", subject).put("target", (Noun) target).put("weapon", getWeaponNoun()).build());
        subject.game().eventBus().post(new SensoryEvent(subject.getArea(), Phrases.get(getHitPhrase(repeatActionCount)), attackContext, this, null, subject, (target instanceof Actor ? (Actor) target : null)));
        Damage damageData = new Damage(damageType, damage, getLimb(), armorMult, targetEffects);
        target.damage(damageData);
        subject.triggerScript("on_attack_success", (target instanceof Actor ? (Actor) target : subject));
    }

    @Override
    public void onFail(Actor subject, AttackTarget target, int repeatActionCount) {
        TextContext attackContext = new TextContext(Map.of("limb", (getLimb() == null ? "null" : getLimb().getName())), new MapBuilder<String, Noun>().put("actor", subject).put("target", (Noun) target).put("weapon", getWeaponNoun()).build());
        subject.game().eventBus().post(new SensoryEvent(subject.getArea(), Phrases.get(getMissPhrase(repeatActionCount)), attackContext, this, null, subject, (target instanceof Actor ? (Actor) target : null)));
        subject.triggerScript("on_attack_failure", (target instanceof Actor ? (Actor) target : subject));
    }

    @Override
    public void onSuccessOverall(Actor subject, int repeatActionCount) {
        TextContext attackContext = new TextContext(Map.of("limb", (getLimb() == null ? "null" : getLimb().getName())), new MapBuilder<String, Noun>().put("actor", subject).put("weapon", getWeaponNoun()).build());
        subject.game().eventBus().post(new SensoryEvent(subject.getArea(), Phrases.get(getHitOverallPhrase(repeatActionCount)), attackContext, this, null, subject, null));
    }

    @Override
    public void onFailOverall(Actor subject, int repeatActionCount) {
        TextContext attackContext = new TextContext(Map.of("limb", (getLimb() == null ? "null" : getLimb().getName())), new MapBuilder<String, Noun>().put("actor", subject).put("weapon", getWeaponNoun()).build());
        subject.game().eventBus().post(new SensoryEvent(subject.getArea(), Phrases.get(getMissOverallPhrase(repeatActionCount)), attackContext, this, null, subject, null));
    }

    @Override
    public int repeatCount(Actor subject) {
        return rate;
    }

    @Override
    public boolean isRepeatMatch(Action action) {
        return action instanceof ActionAttack && ((ActionAttack) action).getWeaponNoun() == this.getWeaponNoun();
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
        if (action instanceof ActionAttack) {
            return ((ActionAttack) action).getWeaponNoun() == this.getWeaponNoun();
        } else {
            return false;
        }
    }

    @Override
    public ActionDetectionChance detectionChance() {
        return ActionDetectionChance.HIGH;
    }

    private String getHitPhrase(int repeatActionCount) {
        return repeatActionCount > 0 && hitPhraseRepeat != null ? hitPhraseRepeat : hitPhrase;
    }

    private String getMissPhrase(int repeatActionCount) {
        return repeatActionCount > 0 && missPhraseRepeat != null ? missPhraseRepeat : missPhrase;
    }

    private String getHitOverallPhrase(int repeatActionCount) {
        return repeatActionCount > 0 && hitOverallPhraseRepeat != null ? hitOverallPhraseRepeat : hitOverallPhrase;
    }

    private String getMissOverallPhrase(int repeatActionCount) {
        return repeatActionCount > 0 && missOverallPhraseRepeat != null ? missOverallPhraseRepeat : missOverallPhrase;
    }

}
