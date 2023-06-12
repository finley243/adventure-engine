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

    public ActionAttackArea(ItemWeapon weapon, Area area, String prompt, String hitPhrase, String hitPhraseRepeat, String hitOverallPhrase, String hitOverallPhraseRepeat, String missPhrase, String missPhraseRepeat, String missOverallPhrase, String missOverallPhraseRepeat, Actor.Skill skill, float baseHitChanceMin, float baseHitChanceMax, float hitChanceBonus, int ammoConsumed, WeaponAttackType.WeaponConsumeType weaponConsumeType, Set<AreaLink.DistanceCategory> ranges, int rate, int damage, String damageType, float armorMult, List<String> targetEffects, float hitChanceMult, boolean canDodge, AttackHitChanceType hitChanceType) {
        super(weapon, area.getAttackTargets(), null, area, prompt, hitPhrase, hitPhraseRepeat, hitOverallPhrase, hitOverallPhraseRepeat, missPhrase, missPhraseRepeat, missOverallPhrase, missOverallPhraseRepeat, skill, baseHitChanceMin, baseHitChanceMax, hitChanceBonus, ammoConsumed, weaponConsumeType, ranges, rate, damage, damageType, armorMult, targetEffects, hitChanceMult, canDodge, hitChanceType);
        this.weapon = weapon;
    }

    @Override
    public void consumeAmmo(Actor subject) {
        if (weapon.getClipSize() > 0) {
            if (weapon.getLoadedAmmoType().isReusable() && weapon.getLoadedAmmoType() != null) {
                getArea().getInventory().addItems(weapon.getLoadedAmmoType().getTemplate().getID(), getAmmoConsumed());
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
    public boolean canChoose(Actor subject) {
        return super.canChoose(subject)
                && (weapon.getClipSize() == 0 || weapon.getAmmoRemaining() >= getAmmoConsumed())
                && getRanges().contains(subject.getArea().getDistanceTo(getArea().getID())) && subject.getArea().isVisible(subject, getArea().getID());
    }

    @Override
    public MenuChoice getMenuChoices(Actor subject) {
        return new MenuChoice(LangUtils.titleCase(getArea().getRelativeName()) + " (" + (getArea().equals(subject.getArea()) ? "" : getArea().getRelativeDirection(subject.getArea()) + ", ") + getChanceTag(subject) + ")", canChoose(subject), new String[]{"Attack", LangUtils.titleCase(weapon.getName()), LangUtils.titleCase(getPrompt())}, new String[]{getPrompt().toLowerCase() + " " + getArea().getRelativeName(), getPrompt().toLowerCase() + " at " + getArea().getRelativeName(), getPrompt().toLowerCase() + " " + weapon.getName() + " at " + getArea().getName(), getPrompt().toLowerCase() + " with " + weapon.getName() + " at " + getArea().getName()});
    }

}
