package com.github.finley243.adventureengine.combat;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.attack.ActionAttackArea;
import com.github.finley243.adventureengine.action.attack.ActionAttackBasic;
import com.github.finley243.adventureengine.action.attack.ActionAttackLimb;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Limb;
import com.github.finley243.adventureengine.item.ItemAmmo;
import com.github.finley243.adventureengine.item.ItemWeapon;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.environment.AreaLink;

import java.util.*;

public class WeaponAttackType {

    public enum AttackCategory {
        SINGLE, TARGETED, SPREAD
    }

    private final String ID;
    private final boolean useAmmoDamage;
    private final AttackCategory category;
    private final String prompt;
    private final String hitPhrase;
    private final String hitPhraseRepeat;
    private final String missPhrase;
    private final String missPhraseRepeat;
    private final int ammoConsumed;
    private final Actor.Skill skillOverride;
    private final boolean useNonIdealRange;
    private final Set<AreaLink.DistanceCategory> rangeOverride;
    private final int rate;
    private final float damageMult;
    private final float hitChanceMult;
    private final boolean canDodge;

    public WeaponAttackType(String ID, boolean useAmmoDamage, AttackCategory category, String prompt, String hitPhrase, String hitPhraseRepeat, String missPhrase, String missPhraseRepeat, int ammoConsumed, Actor.Skill skillOverride, boolean useNonIdealRange, Set<AreaLink.DistanceCategory> rangeOverride, int rate, float damageMult, float hitChanceMult, boolean canDodge) {
        this.ID = ID;
        this.useAmmoDamage = useAmmoDamage;
        this.category = category;
        this.prompt = prompt;
        this.hitPhrase = hitPhrase;
        this.hitPhraseRepeat = hitPhraseRepeat;
        this.missPhrase = missPhrase;
        this.missPhraseRepeat = missPhraseRepeat;
        this.ammoConsumed = ammoConsumed;
        this.skillOverride = skillOverride;
        this.useNonIdealRange = useNonIdealRange;
        this.rangeOverride = rangeOverride;
        this.rate = rate;
        this.damageMult = damageMult;
        this.hitChanceMult = hitChanceMult;
        this.canDodge = canDodge;
    }

    public String getID() {
        return ID;
    }

    public List<Action> generateActions(Actor subject, ItemWeapon weapon) {
        List<Action> actions = new ArrayList<>();
        if (!useAmmoDamage) {
            if (category == AttackCategory.SINGLE) {
                for (Actor target : subject.getVisibleActors()) {
                    if (!target.equals(subject) && !target.isDead()) {
                        actions.add(new ActionAttackBasic(weapon, target, prompt, hitPhrase, hitPhraseRepeat, missPhrase, missPhraseRepeat, Objects.requireNonNullElse(skillOverride, weapon.getSkill()), ammoConsumed, (rangeOverride.isEmpty() ? (useNonIdealRange ? EnumSet.complementOf(EnumSet.copyOf(weapon.getRanges())) : weapon.getRanges()) : rangeOverride), rate, (int) (weapon.getDamage() * (damageMult + 1.0f)), weapon.getDamageType(), weapon.getArmorMult(), weapon.getTargetEffects(), hitChanceMult, canDodge));
                    }
                }
            } else if (category == AttackCategory.TARGETED) {
                for (Actor target : subject.getVisibleActors()) {
                    if (!target.equals(subject) && !target.isDead()) {
                        for (Limb limb : target.getLimbs()) {
                            actions.add(new ActionAttackLimb(weapon, target, limb, prompt, hitPhrase, hitPhraseRepeat, missPhrase, missPhraseRepeat, Objects.requireNonNullElse(skillOverride, weapon.getSkill()), ammoConsumed, (rangeOverride.isEmpty() ? (useNonIdealRange ? EnumSet.complementOf(EnumSet.copyOf(weapon.getRanges())) : weapon.getRanges()) : rangeOverride), rate, (int) (weapon.getDamage() * (damageMult + 1.0f)), weapon.getDamageType(), weapon.getArmorMult(), weapon.getTargetEffects(), hitChanceMult, canDodge));
                        }
                    }
                }
            } else if (category == AttackCategory.SPREAD) {
                for (Area target : subject.getArea().getVisibleAreas(subject)) {
                    actions.add(new ActionAttackArea(weapon, target, prompt, hitPhrase, hitPhraseRepeat, missPhrase, missPhraseRepeat, Objects.requireNonNullElse(skillOverride, weapon.getSkill()), ammoConsumed, (rangeOverride.isEmpty() ? (useNonIdealRange ? EnumSet.complementOf(EnumSet.copyOf(weapon.getRanges())) : weapon.getRanges()) : rangeOverride), rate, (int) (weapon.getDamage() * (damageMult + 1.0f)), weapon.getDamageType(), weapon.getArmorMult(), weapon.getTargetEffects(), hitChanceMult, canDodge));
                }
            }
        } else {
            ItemAmmo ammo = weapon.getLoadedAmmoType();
            if (category == AttackCategory.SINGLE) {
                for (Actor target : subject.getVisibleActors()) {
                    if (!target.equals(subject) && !target.isDead()) {
                        actions.add(new ActionAttackBasic(weapon, target, prompt, hitPhrase, hitPhraseRepeat, missPhrase, missPhraseRepeat, Objects.requireNonNullElse(skillOverride, weapon.getSkill()), ammoConsumed, (rangeOverride.isEmpty() ? (useNonIdealRange ? EnumSet.complementOf(EnumSet.copyOf(weapon.getRanges())) : weapon.getRanges()) : rangeOverride), rate, (int) ((weapon.getDamage() + ammo.getDamage()) * (damageMult + 1.0f)), ammo.getDamageType(), ammo.getArmorMult(), ammo.getTargetEffects(), hitChanceMult, canDodge));
                    }
                }
            } else if (category == AttackCategory.TARGETED) {
                for (Actor target : subject.getVisibleActors()) {
                    if (!target.equals(subject) && !target.isDead()) {
                        for (Limb limb : target.getLimbs()) {
                            actions.add(new ActionAttackLimb(weapon, target, limb, prompt, hitPhrase, hitPhraseRepeat, missPhrase, missPhraseRepeat, Objects.requireNonNullElse(skillOverride, weapon.getSkill()), ammoConsumed, (rangeOverride.isEmpty() ? (useNonIdealRange ? EnumSet.complementOf(EnumSet.copyOf(weapon.getRanges())) : weapon.getRanges()) : rangeOverride), rate, (int) ((weapon.getDamage() + ammo.getDamage()) * (damageMult + 1.0f)), ammo.getDamageType(), ammo.getArmorMult(), ammo.getTargetEffects(), hitChanceMult, canDodge));
                        }
                    }
                }
            } else if (category == AttackCategory.SPREAD) {
                for (Area target : subject.getArea().getVisibleAreas(subject)) {
                    actions.add(new ActionAttackArea(weapon, target, prompt, hitPhrase, hitPhraseRepeat, missPhrase, missPhraseRepeat, Objects.requireNonNullElse(skillOverride, weapon.getSkill()), ammoConsumed, (rangeOverride.isEmpty() ? (useNonIdealRange ? EnumSet.complementOf(EnumSet.copyOf(weapon.getRanges())) : weapon.getRanges()) : rangeOverride), rate, (int) ((weapon.getDamage() + ammo.getDamage()) * (damageMult + 1.0f)), ammo.getDamageType(), ammo.getArmorMult(), ammo.getTargetEffects(), hitChanceMult, canDodge));
                }
            }
        }
        return actions;
    }

}
