package com.github.finley243.adventureengine.combat;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.attack.ActionAttack;
import com.github.finley243.adventureengine.action.attack.ActionAttackArea;
import com.github.finley243.adventureengine.action.attack.ActionAttackBasic;
import com.github.finley243.adventureengine.action.attack.ActionAttackLimb;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Limb;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.ItemWeapon;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.environment.AreaLink;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class WeaponAttackType {

    public enum AttackCategory {
        SINGLE, TARGETED, SPREAD
    }

    private final String ID;
    private final AttackCategory category;
    private final String prompt;
    private final String hitPhrase;
    private final String hitPhraseRepeat;
    private final String hitOverallPhrase;
    private final String hitOverallPhraseRepeat;
    private final String missPhrase;
    private final String missPhraseRepeat;
    private final String missOverallPhrase;
    private final String missOverallPhraseRepeat;
    private final int ammoConsumed;
    private final Actor.Skill skillOverride;
    private final Float baseHitChanceMin;
    private final Float baseHitChanceMax;
    private final boolean useNonIdealRange;
    private final Set<AreaLink.DistanceCategory> rangeOverride;
    private final Integer rateOverride;
    private final Integer damageOverride;
    private final float damageMult;
    private final Damage.DamageType damageTypeOverride;
    private final Float armorMultOverride;
    private final List<String> targetEffects;
    private final float hitChanceMult;
    private final boolean canDodge;
    private final ActionAttack.AttackHitChanceType hitChanceType;

    public WeaponAttackType(String ID, AttackCategory category, String prompt, String hitPhrase, String hitPhraseRepeat, String hitOverallPhrase, String hitOverallPhraseRepeat, String missPhrase, String missPhraseRepeat, String missOverallPhrase, String missOverallPhraseRepeat, int ammoConsumed, Actor.Skill skillOverride, Float baseHitChanceMin, Float baseHitChanceMax, boolean useNonIdealRange, Set<AreaLink.DistanceCategory> rangeOverride, Integer rateOverride, Integer damageOverride, float damageMult, Damage.DamageType damageTypeOverride, Float armorMultOverride, List<String> targetEffects, float hitChanceMult, boolean canDodge, ActionAttack.AttackHitChanceType hitChanceType) {
        this.ID = ID;
        this.category = category;
        this.prompt = prompt;
        this.hitPhrase = hitPhrase;
        this.hitPhraseRepeat = hitPhraseRepeat;
        this.hitOverallPhrase = hitOverallPhrase;
        this.hitOverallPhraseRepeat = hitOverallPhraseRepeat;
        this.missPhrase = missPhrase;
        this.missPhraseRepeat = missPhraseRepeat;
        this.missOverallPhrase = missOverallPhrase;
        this.missOverallPhraseRepeat = missOverallPhraseRepeat;
        this.ammoConsumed = ammoConsumed;
        this.skillOverride = skillOverride;
        this.baseHitChanceMin = baseHitChanceMin;
        this.baseHitChanceMax = baseHitChanceMax;
        this.useNonIdealRange = useNonIdealRange;
        this.rangeOverride = rangeOverride;
        this.rateOverride = rateOverride;
        this.damageOverride = damageOverride;
        this.damageMult = damageMult;
        this.damageTypeOverride = damageTypeOverride;
        this.armorMultOverride = armorMultOverride;
        this.targetEffects = targetEffects;
        this.hitChanceMult = hitChanceMult;
        this.canDodge = canDodge;
        this.hitChanceType = hitChanceType;
    }

    public String getID() {
        return ID;
    }

    public List<Action> generateActions(Actor subject, ItemWeapon weapon, Item item) {
        if (weapon == null && (skillOverride == null || baseHitChanceMin == null || baseHitChanceMax == null || rangeOverride.isEmpty() || rateOverride == null || damageOverride == null || damageTypeOverride == null || armorMultOverride == null)) {
            throw new UnsupportedOperationException("Attack type missing overrides, cannot use without weapon: " + getID());
        }
        List<Action> actions = new ArrayList<>();
        Actor.Skill skill = weapon == null ? skillOverride : weapon.getSkill();
        float hitChanceMin = weapon == null ? baseHitChanceMin : weapon.getBaseHitChanceMin();
        float hitChanceMax = weapon == null ? baseHitChanceMax : weapon.getBaseHitChanceMax();
        float accuracyBonus = weapon == null ? 0.0f : weapon.getAccuracyBonus();
        Set<AreaLink.DistanceCategory> ranges = weapon == null ? rangeOverride : (useNonIdealRange ? EnumSet.complementOf(EnumSet.copyOf(weapon.getRanges())) : weapon.getRanges());
        int rate = weapon == null ? rateOverride : weapon.getRate();
        int damage = weapon == null ? damageOverride : (int) (weapon.getDamage() * (damageMult + 1.0f));
        Damage.DamageType damageType = weapon == null ? damageTypeOverride : weapon.getDamageType();
        float armorMult = weapon == null ? armorMultOverride : weapon.getArmorMult();
        List<String> targetEffectsCombined = new ArrayList<>(targetEffects);
        if (weapon != null) {
            targetEffectsCombined.addAll(weapon.getTargetEffects());
        }
        if (category == AttackCategory.SINGLE) {
            for (Actor target : subject.getVisibleActors()) {
                if (!target.equals(subject) && !target.isDead()) {
                    actions.add(new ActionAttackBasic(item, target, prompt, hitPhrase, hitPhraseRepeat, hitOverallPhrase, hitOverallPhraseRepeat, missPhrase, missPhraseRepeat, missOverallPhrase, missOverallPhraseRepeat, skill, hitChanceMin, hitChanceMax, accuracyBonus, ammoConsumed, ranges, rate, damage, damageType, armorMult, targetEffectsCombined, hitChanceMult, canDodge, hitChanceType));
                }
            }
        } else if (category == AttackCategory.TARGETED) {
            for (Actor target : subject.getVisibleActors()) {
                if (!target.equals(subject) && !target.isDead()) {
                    for (Limb limb : target.getLimbs()) {
                        actions.add(new ActionAttackLimb(item, target, limb, prompt, hitPhrase, hitPhraseRepeat, hitOverallPhrase, hitOverallPhraseRepeat, missPhrase, missPhraseRepeat, missOverallPhrase, missOverallPhraseRepeat, skill, hitChanceMin, hitChanceMax, accuracyBonus, ammoConsumed, ranges, rate, damage, damageType, armorMult, targetEffectsCombined, hitChanceMult, canDodge, hitChanceType));
                    }
                }
            }
        } else if (category == AttackCategory.SPREAD) {
            // Possibly switch to only areas visible to the subject?
            for (Area target : subject.getArea().getLineOfSightAreas()) {
                actions.add(new ActionAttackArea(item, target, prompt, hitPhrase, hitPhraseRepeat, hitOverallPhrase, hitOverallPhraseRepeat, missPhrase, missPhraseRepeat, missOverallPhrase, missOverallPhraseRepeat, skill, hitChanceMin, hitChanceMax, accuracyBonus, ammoConsumed, ranges, rate, damage, damageType, armorMult, targetEffectsCombined, hitChanceMult, canDodge, hitChanceType));
            }
        }
        return actions;
    }

}
