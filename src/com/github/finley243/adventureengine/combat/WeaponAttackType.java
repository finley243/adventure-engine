package com.github.finley243.adventureengine.combat;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.attack.ActionAttack;
import com.github.finley243.adventureengine.action.attack.ActionAttackArea;
import com.github.finley243.adventureengine.action.attack.ActionAttackBasic;
import com.github.finley243.adventureengine.action.attack.ActionAttackLimb;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Limb;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.component.ItemComponentWeapon;
import com.github.finley243.adventureengine.world.AttackTarget;
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

    public enum WeaponConsumeType {
        NONE, PLACE, DESTROY
    }

    private final String ID;
    private final AttackCategory category;
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
    private final int ammoConsumed;
    private final int actionPoints;
    private final WeaponConsumeType weaponConsumeType;

    private final String skillOverride;
    private final Float baseHitChanceMinOverride;
    private final Float baseHitChanceMaxOverride;
    private final boolean useNonIdealRange;
    private final Set<AreaLink.DistanceCategory> rangeOverride;
    private final Integer rateOverride;
    private final Integer damageOverride;
    private final float damageMult;
    private final String damageTypeOverride;
    private final Float armorMultOverride;
    private final List<String> targetEffects;
    private final boolean overrideTargetEffects;
    private final float hitChanceMult;
    private final String dodgeSkill;
    private final ActionAttack.AttackHitChanceType hitChanceType;
    private final Boolean isLoudOverride;

    public WeaponAttackType(String ID, AttackCategory category, String prompt, String hitPhrase, String hitPhraseRepeat, String hitOverallPhrase, String hitOverallPhraseRepeat, String hitPhraseAudible, String hitPhraseRepeatAudible, String hitOverallPhraseAudible, String hitOverallPhraseRepeatAudible, String missPhrase, String missPhraseRepeat, String missOverallPhrase, String missOverallPhraseRepeat, String missPhraseAudible, String missPhraseRepeatAudible, String missOverallPhraseAudible, String missOverallPhraseRepeatAudible, int ammoConsumed, int actionPoints, WeaponConsumeType weaponConsumeType, String skillOverride, Float baseHitChanceMinOverride, Float baseHitChanceMaxOverride, boolean useNonIdealRange, Set<AreaLink.DistanceCategory> rangeOverride, Integer rateOverride, Integer damageOverride, float damageMult, String damageTypeOverride, Float armorMultOverride, List<String> targetEffects, boolean overrideTargetEffects, float hitChanceMult, String dodgeSkill, ActionAttack.AttackHitChanceType hitChanceType, Boolean isLoudOverride) {
        this.ID = ID;
        this.category = category;
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
        this.ammoConsumed = ammoConsumed;
        this.actionPoints = actionPoints;
        this.weaponConsumeType = weaponConsumeType;
        this.skillOverride = skillOverride;
        this.baseHitChanceMinOverride = baseHitChanceMinOverride;
        this.baseHitChanceMaxOverride = baseHitChanceMaxOverride;
        this.useNonIdealRange = useNonIdealRange;
        this.rangeOverride = rangeOverride;
        this.rateOverride = rateOverride;
        this.damageOverride = damageOverride;
        this.damageMult = damageMult;
        this.damageTypeOverride = damageTypeOverride;
        this.armorMultOverride = armorMultOverride;
        this.targetEffects = targetEffects;
        this.overrideTargetEffects = overrideTargetEffects;
        this.hitChanceMult = hitChanceMult;
        this.dodgeSkill = dodgeSkill;
        this.hitChanceType = hitChanceType;
        this.isLoudOverride = isLoudOverride;
    }

    public String getID() {
        return ID;
    }

    public List<Action> generateActions(Actor subject, Item weapon) {
        if (weapon == null) throw new IllegalArgumentException("Weapon cannot be null");
        Context context = new Context(subject.game(), subject, subject, weapon);
        List<Action> actions = new ArrayList<>();
        String skill = skillOverride != null ? skillOverride : weapon.getComponentOfType(ItemComponentWeapon.class).getSkill();
        float hitChanceMin = baseHitChanceMinOverride != null ? baseHitChanceMinOverride : weapon.getComponentOfType(ItemComponentWeapon.class).getBaseHitChanceMin();
        float hitChanceMax = baseHitChanceMaxOverride != null ? baseHitChanceMaxOverride : weapon.getComponentOfType(ItemComponentWeapon.class).getBaseHitChanceMax();
        Set<AreaLink.DistanceCategory> ranges = rangeOverride != null && !rangeOverride.isEmpty() ? rangeOverride : (useNonIdealRange ? EnumSet.complementOf(EnumSet.copyOf(weapon.getComponentOfType(ItemComponentWeapon.class).getRanges(context))) : weapon.getComponentOfType(ItemComponentWeapon.class).getRanges(context));
        int rate = rateOverride != null ? rateOverride : weapon.getComponentOfType(ItemComponentWeapon.class).getRate(context);
        int damage = damageOverride != null ? damageOverride : (int) (weapon.getComponentOfType(ItemComponentWeapon.class).getDamage(context) * (damageMult + 1.0f));
        String damageType = damageTypeOverride != null ? damageTypeOverride : weapon.getComponentOfType(ItemComponentWeapon.class).getDamageType(context);
        float armorMult = armorMultOverride != null ? armorMultOverride : weapon.getComponentOfType(ItemComponentWeapon.class).getArmorMult(context);
        boolean isLoud = isLoudOverride != null ? isLoudOverride : weapon.getComponentOfType(ItemComponentWeapon.class).isLoud(context);
        List<String> targetEffectsCombined = new ArrayList<>(targetEffects);
        if (!overrideTargetEffects) {
            targetEffectsCombined.addAll(weapon.getComponentOfType(ItemComponentWeapon.class).getTargetEffects(context));
        }
        switch (category) {
            case SINGLE -> {
                for (AttackTarget target : subject.getLineOfSightAttackTargets()) {
                    if (!target.equals(subject) && target.isVisible(subject) && target.canBeAttacked()) {
                        actions.add(new ActionAttackBasic(this, weapon, target, prompt, hitPhrase, hitPhraseRepeat, hitOverallPhrase, hitOverallPhraseRepeat, hitPhraseAudible, hitPhraseRepeatAudible, hitOverallPhraseAudible, hitOverallPhraseRepeatAudible, missPhrase, missPhraseRepeat, missOverallPhrase, missOverallPhraseRepeat, missPhraseAudible, missPhraseRepeatAudible, missOverallPhraseAudible, missOverallPhraseRepeatAudible, skill, hitChanceMin, hitChanceMax, ammoConsumed, actionPoints, weaponConsumeType, ranges, rate, damage, damageType, armorMult, targetEffectsCombined, hitChanceMult, dodgeSkill, hitChanceType, isLoud));
                    }
                }
            }
            case TARGETED -> {
                for (Actor target : subject.getLineOfSightActors()) {
                    if (!target.equals(subject) && target.isVisible(subject) && target.canBeAttacked()) {
                        for (Limb limb : target.getLimbs()) {
                            actions.add(new ActionAttackLimb(this, weapon, target, limb, prompt, hitPhrase, hitPhraseRepeat, hitOverallPhrase, hitOverallPhraseRepeat, hitPhraseAudible, hitPhraseRepeatAudible, hitOverallPhraseAudible, hitOverallPhraseRepeatAudible, missPhrase, missPhraseRepeat, missOverallPhrase, missOverallPhraseRepeat, missPhraseAudible, missPhraseRepeatAudible, missOverallPhraseAudible, missOverallPhraseRepeatAudible, skill, hitChanceMin, hitChanceMax, ammoConsumed, actionPoints, weaponConsumeType, ranges, rate, damage, damageType, armorMult, targetEffectsCombined, hitChanceMult, dodgeSkill, hitChanceType, isLoud));
                        }
                    }
                }
            }
            case SPREAD -> {
                for (Area target : subject.getVisibleAreas().keySet()) {
                    actions.add(new ActionAttackArea(this, weapon, target, prompt, hitPhrase, hitPhraseRepeat, hitOverallPhrase, hitOverallPhraseRepeat, hitPhraseAudible, hitPhraseRepeatAudible, hitOverallPhraseAudible, hitOverallPhraseRepeatAudible, missPhrase, missPhraseRepeat, missOverallPhrase, missOverallPhraseRepeat, missPhraseAudible, missPhraseRepeatAudible, missOverallPhraseAudible, missOverallPhraseRepeatAudible, skill, hitChanceMin, hitChanceMax, ammoConsumed, actionPoints, weaponConsumeType, ranges, rate, damage, damageType, armorMult, targetEffectsCombined, hitChanceMult, dodgeSkill, hitChanceType, isLoud));
                }
            }
        }
        return actions;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof WeaponAttackType attackType && getID().equals(attackType.getID());
    }

}
