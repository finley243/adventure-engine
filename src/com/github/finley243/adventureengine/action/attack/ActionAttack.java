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
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.ItemWeapon;
import com.github.finley243.adventureengine.textgen.TextContext;
import com.github.finley243.adventureengine.textgen.Noun;
import com.github.finley243.adventureengine.MapBuilder;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.AttackTarget;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.environment.AreaLink;

import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class ActionAttack extends ActionRandomEach<AttackTarget> {

    public enum AttackHitChanceType {
        INDEPENDENT, JOINT
    }

    private final Set<AttackTarget> targets;
    private final ItemWeapon weapon;
    private final Limb limb;
    private final Area area;
    private final String prompt;
    private final String hitPhrase;
    private final String hitPhraseRepeat;
    private final String hitOverallPhrase;
    private final String hitOverallPhraseRepeat;
    private final String hitPhraseAudible;
    private final String hitPhraseRepeatAudible;
    private final String hitOverallPhraseAudible;
    private final String hitOverallPhraseRepeatAudible;
    private final String missPhrase;
    private final String missPhraseRepeat;
    private final String missOverallPhrase;
    private final String missOverallPhraseRepeat;
    private final String missPhraseAudible;
    private final String missPhraseRepeatAudible;
    private final String missOverallPhraseAudible;
    private final String missOverallPhraseRepeatAudible;
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

    public ActionAttack(ItemWeapon weapon, Set<AttackTarget> targets, Limb limb, Area area, String prompt, String hitPhrase, String hitPhraseRepeat, String hitOverallPhrase, String hitOverallPhraseRepeat, String hitPhraseAudible, String hitPhraseRepeatAudible, String hitOverallPhraseAudible, String hitOverallPhraseRepeatAudible, String missPhrase, String missPhraseRepeat, String missOverallPhrase, String missOverallPhraseRepeat, String missPhraseAudible, String missPhraseRepeatAudible, String missOverallPhraseAudible, String missOverallPhraseRepeatAudible, String attackSkill, float baseHitChanceMin, float baseHitChanceMax, int ammoConsumed, int actionPoints, WeaponAttackType.WeaponConsumeType weaponConsumeType, Set<AreaLink.DistanceCategory> ranges, int rate, int damage, String damageType, float armorMult, List<String> targetEffects, float hitChanceMult, String dodgeSkill, AttackHitChanceType hitChanceType, boolean isLoud) {
        super(targets);
        this.weapon = weapon;
        this.targets = targets;
        this.limb = limb;
        this.area = area;
        this.prompt = prompt;
        this.hitPhrase = hitPhrase;
        this.hitPhraseRepeat = hitPhraseRepeat;
        this.hitOverallPhrase = hitOverallPhrase;
        this.hitOverallPhraseRepeat = hitOverallPhraseRepeat;
        this.hitPhraseAudible = hitPhraseAudible;
        this.hitPhraseRepeatAudible = hitPhraseRepeatAudible;
        this.hitOverallPhraseAudible = hitOverallPhraseAudible;
        this.hitOverallPhraseRepeatAudible = hitOverallPhraseRepeatAudible;
        this.missPhrase = missPhrase;
        this.missPhraseRepeat = missPhraseRepeat;
        this.missOverallPhrase = missOverallPhrase;
        this.missOverallPhraseRepeat = missOverallPhraseRepeat;
        this.missPhraseAudible = missPhraseAudible;
        this.missPhraseRepeatAudible = missPhraseRepeatAudible;
        this.missOverallPhraseAudible = missOverallPhraseAudible;
        this.missOverallPhraseRepeatAudible = missOverallPhraseRepeatAudible;
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

    public abstract void consumeAmmo(Actor subject);

    public String getPrompt() {
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
        // TODO - Allow setting dodge/counter skill type in the attack definition
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
        TextContext attackContext = new TextContext(Map.of("limb", (getLimb() == null ? "null" : getLimb().getName()), "relativeTo", (getArea() == null ? "null" : getArea().getRelativeName())), new MapBuilder<String, Noun>().put("actor", subject).put("target", (Noun) target).put("weapon", getWeapon()).putIf(getArea() != null, "area", getArea()).build());
        subject.game().eventBus().post(new SensoryEvent(subject.getArea(), Phrases.get(getHitPhrase(repeatActionCount)), Phrases.get(getHitPhraseAudible(repeatActionCount)), attackContext, isLoud, this, null, subject, (target instanceof Actor ? (Actor) target : null)));
        Damage damageData = new Damage(damageType, damage, getLimb(), armorMult, targetEffects);
        target.damage(damageData, new Context(subject.game(), subject, target, getWeapon()));
        subject.triggerScript("on_attack_success", new Context(subject.game(), subject, target));
    }

    @Override
    public void onFail(Actor subject, AttackTarget target, int repeatActionCount) {
        TextContext attackContext = new TextContext(Map.of("limb", (getLimb() == null ? "null" : getLimb().getName()), "relativeTo", (getArea() == null ? "null" : getArea().getRelativeName())), new MapBuilder<String, Noun>().put("actor", subject).put("target", (Noun) target).put("weapon", getWeapon()).putIf(getArea() != null, "area", getArea()).build());
        subject.game().eventBus().post(new SensoryEvent(subject.getArea(), Phrases.get(getMissPhrase(repeatActionCount)), Phrases.get(getMissPhraseAudible(repeatActionCount)), attackContext, isLoud, this, null, subject, (target instanceof Actor ? (Actor) target : null)));
        subject.triggerScript("on_attack_failure", new Context(subject.game(), subject, target));
    }

    @Override
    public void onSuccessOverall(Actor subject, int repeatActionCount) {
        TextContext attackContext = new TextContext(Map.of("limb", (getLimb() == null ? "null" : getLimb().getName()), "relativeTo", (getArea() == null ? "null" : getArea().getRelativeName())), new MapBuilder<String, Noun>().put("actor", subject).put("weapon", getWeapon()).putIf(getArea() != null, "area", getArea()).build());
        subject.game().eventBus().post(new SensoryEvent(subject.getArea(), Phrases.get(getHitOverallPhrase(repeatActionCount)), Phrases.get(getHitOverallPhraseAudible(repeatActionCount)), attackContext, isLoud, this, null, subject, null));
    }

    @Override
    public void onFailOverall(Actor subject, int repeatActionCount) {
        TextContext attackContext = new TextContext(Map.of("limb", (getLimb() == null ? "null" : getLimb().getName()), "relativeTo", (getArea() == null ? "null" : getArea().getRelativeName())), new MapBuilder<String, Noun>().put("actor", subject).put("weapon", getWeapon()).putIf(getArea() != null, "area", getArea()).build());
        subject.game().eventBus().post(new SensoryEvent(subject.getArea(), Phrases.get(getMissOverallPhrase(repeatActionCount)), Phrases.get(getMissOverallPhraseAudible(repeatActionCount)), attackContext, isLoud, this, null, subject, null));
    }

    @Override
    public int repeatCount(Actor subject) {
        return rate;
    }

    @Override
    public boolean isRepeatMatch(Action action) {
        return action instanceof ActionAttack && ((ActionAttack) action).getWeapon() == this.getWeapon();
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
        if (action instanceof ActionAttack) {
            return ((ActionAttack) action).getWeapon() == this.getWeapon();
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

    private String getHitPhraseAudible(int repeatActionCount) {
        return repeatActionCount > 0 && hitPhraseRepeatAudible != null ? hitPhraseRepeatAudible : hitPhraseAudible;
    }

    private String getMissPhraseAudible(int repeatActionCount) {
        return repeatActionCount > 0 && missPhraseRepeatAudible != null ? missPhraseRepeatAudible : missPhraseAudible;
    }

    private String getHitOverallPhraseAudible(int repeatActionCount) {
        return repeatActionCount > 0 && hitOverallPhraseRepeatAudible != null ? hitOverallPhraseRepeatAudible : hitOverallPhraseAudible;
    }

    private String getMissOverallPhraseAudible(int repeatActionCount) {
        return repeatActionCount > 0 && missOverallPhraseRepeatAudible != null ? missOverallPhraseRepeatAudible : missOverallPhraseAudible;
    }

}
