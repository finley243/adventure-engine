package com.github.finley243.adventureengine.action.attack;

import com.github.finley243.adventureengine.combat.CombatHelper;
import com.github.finley243.adventureengine.combat.Damage;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionRandomEach;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Limb;
import com.github.finley243.adventureengine.effect.Effect;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Noun;
import com.github.finley243.adventureengine.textgen.NounMapper;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.AttackTarget;
import com.github.finley243.adventureengine.world.environment.AreaLink;

import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class ActionAttack extends ActionRandomEach<AttackTarget> {

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
    private final Actor.Skill skill;
    private final float baseHitChanceMin;
    private final float baseHitChanceMax;
    private final float hitChanceBonus;
    private final int ammoConsumed;
    private final Set<AreaLink.DistanceCategory> ranges;
    private final int rate;
    private final int damage;
    private final Damage.DamageType damageType;
    private final float armorMult;
    private final List<Effect> targetEffects;
    private final float hitChanceMult;
    private final boolean canDodge;

    public ActionAttack(Noun weaponNoun, Set<AttackTarget> targets, Limb limb, String prompt, String hitPhrase, String hitPhraseRepeat, String hitOverallPhrase, String hitOverallPhraseRepeat, String missPhrase, String missPhraseRepeat, String missOverallPhrase, String missOverallPhraseRepeat, Actor.Skill skill, float baseHitChanceMin, float baseHitChanceMax, float hitChanceBonus, int ammoConsumed, Set<AreaLink.DistanceCategory> ranges, int rate, int damage, Damage.DamageType damageType, float armorMult, List<Effect> targetEffects, float hitChanceMult, boolean canDodge) {
        super(ActionDetectionChance.HIGH, targets);
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
        this.skill = skill;
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
    }

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

    @Override
    public float chance(Actor subject, AttackTarget target) {
        return CombatHelper.calculateHitChance(subject, target, getLimb(), getSkill(), baseHitChanceMin, baseHitChanceMax, hitChanceBonus, canDodge(), hitChanceMult());
    }

    public abstract void consumeAmmo(Actor subject);

    @Override
    public float chanceOverall(Actor subject) {
        return 1.0f;
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
            if (target instanceof Actor && ((Actor) target).targetingComponent() != null) {
                ((Actor) target).targetingComponent().addCombatant(subject);
            }
        }
    }

    @Override
    public void onSuccess(Actor subject, AttackTarget target, int repeatActionCount) {
        int damage = damage();
        Context attackContext = new Context(Map.of("limb", (getLimb() == null ? "null" : getLimb().getName())), new NounMapper().put("actor", subject).put("target", (Noun) target).put("weapon", getWeaponNoun()).build());
        subject.game().eventBus().post(new SensoryEvent(subject.getArea(), Phrases.get(getHitPhrase(repeatActionCount)), attackContext, this, null, subject, (target instanceof Actor ? (Actor) target : null)));
        Damage damageData = new Damage(damageType, damage, getLimb(), armorMult, targetEffects);
        target.damage(damageData);
        subject.triggerScript("on_attack_success", (target instanceof Actor ? (Actor) target : subject));
    }

    @Override
    public void onFail(Actor subject, AttackTarget target, int repeatActionCount) {
        Context attackContext = new Context(Map.of("limb", (getLimb() == null ? "null" : getLimb().getName())), new NounMapper().put("actor", subject).put("target", (Noun) target).put("weapon", getWeaponNoun()).build());
        subject.game().eventBus().post(new SensoryEvent(subject.getArea(), Phrases.get(getMissPhrase(repeatActionCount)), attackContext, this, null, subject, (target instanceof Actor ? (Actor) target : null)));
        subject.triggerScript("on_attack_failure", (target instanceof Actor ? (Actor) target : subject));
    }

    @Override
    public void onSuccessOverall(Actor subject, int repeatActionCount) {
        Context attackContext = new Context(Map.of("limb", (getLimb() == null ? "null" : getLimb().getName())), new NounMapper().put("actor", subject).put("weapon", getWeaponNoun()).build());
        subject.game().eventBus().post(new SensoryEvent(subject.getArea(), Phrases.get(getHitOverallPhrase(repeatActionCount)), attackContext, this, null, subject, null));
    }

    @Override
    public void onFailOverall(Actor subject, int repeatActionCount) {
        Context attackContext = new Context(Map.of("limb", (getLimb() == null ? "null" : getLimb().getName())), new NounMapper().put("actor", subject).put("weapon", getWeaponNoun()).build());
        subject.game().eventBus().post(new SensoryEvent(subject.getArea(), Phrases.get(getMissOverallPhrase(repeatActionCount)), attackContext, this, null, subject, null));
    }

    public int getAmmoConsumed() {
        return ammoConsumed;
    }

    public Actor.Skill getSkill() {
        return skill;
    }

    public Set<AreaLink.DistanceCategory> getRanges() {
        return ranges;
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
        if (subject.targetingComponent() == null) return 0.0f;
        for (AttackTarget target : targets) {
            if (target instanceof Actor && subject.targetingComponent().isCombatant((Actor) target)) {
                return 0.8f;
            }
        }
        return 0.0f;
    }

    @Override
    public boolean isBlockedMatch(Action action) {
        if(action instanceof ActionAttack) {
            return ((ActionAttack) action).getWeaponNoun() == this.getWeaponNoun();
        } else {
            return false;
        }
    }

    @Override
    public ActionResponseType responseType() {
        return ActionResponseType.ATTACK;
    }

    private String getHitPhrase(int repeatActionCount) {
        return repeatActionCount > 0 ? hitPhraseRepeat : hitPhrase;
    }

    private String getMissPhrase(int repeatActionCount) {
        return repeatActionCount > 0 ? missPhraseRepeat : missPhrase;
    }

    private String getHitOverallPhrase(int repeatActionCount) {
        return repeatActionCount > 0 ? hitOverallPhraseRepeat : hitOverallPhrase;
    }

    private String getMissOverallPhrase(int repeatActionCount) {
        return repeatActionCount > 0 ? missOverallPhraseRepeat : missOverallPhrase;
    }

}
