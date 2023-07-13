package com.github.finley243.adventureengine.action.attack;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.combat.WeaponAttackType;
import com.github.finley243.adventureengine.item.ItemWeapon;
import com.github.finley243.adventureengine.menu.MenuChoice;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.environment.AreaLink;

import java.util.List;
import java.util.Set;

public class ActionAttackArea extends ActionAttack {

    private final ItemWeapon weapon;

    public ActionAttackArea(ItemWeapon weapon, Area area, String prompt, String hitPhrase, String hitPhraseRepeat, String hitOverallPhrase, String hitOverallPhraseRepeat, String hitPhraseAudible, String hitPhraseRepeatAudible, String hitOverallPhraseAudible, String hitOverallPhraseRepeatAudible, String missPhrase, String missPhraseRepeat, String missOverallPhrase, String missOverallPhraseRepeat, String missPhraseAudible, String missPhraseRepeatAudible, String missOverallPhraseAudible, String missOverallPhraseRepeatAudible, String skill, float baseHitChanceMin, float baseHitChanceMax, int ammoConsumed, WeaponAttackType.WeaponConsumeType weaponConsumeType, Set<AreaLink.DistanceCategory> ranges, int rate, int damage, String damageType, float armorMult, List<String> targetEffects, float hitChanceMult, String dodgeSkill, AttackHitChanceType hitChanceType, boolean isLoud) {
        super(weapon, area.getAttackTargets(), null, area, prompt, hitPhrase, hitPhraseRepeat, hitOverallPhrase, hitOverallPhraseRepeat, hitPhraseAudible, hitPhraseRepeatAudible, hitOverallPhraseAudible, hitOverallPhraseRepeatAudible, missPhrase, missPhraseRepeat, missOverallPhrase, missOverallPhraseRepeat, missPhraseAudible, missPhraseRepeatAudible, missOverallPhraseAudible, missOverallPhraseRepeatAudible, skill, baseHitChanceMin, baseHitChanceMax, ammoConsumed, weaponConsumeType, ranges, rate, damage, damageType, armorMult, targetEffects, hitChanceMult, dodgeSkill, hitChanceType, isLoud);
        this.weapon = weapon;
    }

    @Override
    public void consumeAmmo(Actor subject) {
        if (weapon.usesAmmo()) {
            if (weapon.getLoadedAmmoType().isReusable() && weapon.getLoadedAmmoType() != null) {
                getArea().getInventory().addItems(weapon.getLoadedAmmoType().getTemplateID(), getAmmoConsumed());
            }
            weapon.consumeAmmo(getAmmoConsumed());
        }
        switch (getWeaponConsumeType()) {
            case PLACE -> {
                subject.getInventory().removeItem(weapon);
                getArea().getInventory().addItem(weapon);
            }
            case DESTROY -> subject.getInventory().removeItem(weapon);
        }
    }

    @Override
    public CanChooseResult canChoose(Actor subject) {
        CanChooseResult resultSuper = super.canChoose(subject);
        if (!resultSuper.canChoose()) {
            return resultSuper;
        }
        if (weapon.usesAmmo() && weapon.getAmmoRemaining() < getAmmoConsumed()) {
            return new CanChooseResult(false, "Not enough ammo");
        }
        if (!getRanges().contains(subject.getArea().getDistanceTo(getArea().getID()))) {
            return new CanChooseResult(false, "Target area outside range");
        }
        if (!subject.getArea().isVisible(subject, getArea().getID())) {
            return new CanChooseResult(false, "Target area not visible");
        }
        return new CanChooseResult(true, null);
    }

    @Override
    public MenuChoice getMenuChoices(Actor subject) {
        return new MenuChoice(LangUtils.titleCase(getArea().getRelativeName()) + " (" + (getArea().equals(subject.getArea()) ? "" : getArea().getRelativeDirection(subject.getArea()) + ", ") + getChanceTag(subject) + ")", canChoose(subject).canChoose(), new String[]{"Attack", LangUtils.titleCase(weapon.getName()), LangUtils.titleCase(getPrompt())}, new String[]{getPrompt().toLowerCase() + " " + getArea().getRelativeName(), getPrompt().toLowerCase() + " at " + getArea().getRelativeName(), getPrompt().toLowerCase() + " " + weapon.getName() + " at " + getArea().getName(), getPrompt().toLowerCase() + " with " + weapon.getName() + " at " + getArea().getName()});
    }

}
