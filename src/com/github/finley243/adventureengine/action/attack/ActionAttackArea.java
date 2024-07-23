package com.github.finley243.adventureengine.action.attack;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.combat.WeaponAttackType;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.component.ItemComponentAmmo;
import com.github.finley243.adventureengine.item.component.ItemComponentMagazine;
import com.github.finley243.adventureengine.menu.action.MenuData;
import com.github.finley243.adventureengine.menu.action.MenuDataAttackArea;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.environment.AreaLink;

import java.util.List;
import java.util.Set;

public class ActionAttackArea extends ActionAttack {

    private final Item weapon;

    public ActionAttackArea(WeaponAttackType attackType, Item weapon, Area area, String prompt, String attackPhrase, String attackOverallPhrase, String attackPhraseAudible, String attackOverallPhraseAudible, int ammoConsumed, int actionPoints, WeaponAttackType.WeaponConsumeType weaponConsumeType, Set<AreaLink.DistanceCategory> ranges, int rate, Script damage, String damageType, float armorMult, List<String> targetEffects, Script hitChanceExpression, Script hitChanceOverallExpression, float hitChanceMult, AttackHitChanceType hitChanceType, boolean isLoud) {
        super(attackType, weapon, area.getAttackTargets(), null, area, prompt, attackPhrase, attackOverallPhrase, attackPhraseAudible, attackOverallPhraseAudible, ammoConsumed, actionPoints, weaponConsumeType, ranges, rate, damage, damageType, armorMult, targetEffects, hitChanceExpression, hitChanceOverallExpression, hitChanceMult, hitChanceType, isLoud);
        this.weapon = weapon;
    }

    @Override
    public void consumeAmmo(Actor subject) {
        if (weapon == null) {
            return;
        }
        if (weapon.hasComponentOfType(ItemComponentMagazine.class)) {
            if (weapon.getComponentOfType(ItemComponentMagazine.class).getLoadedAmmoType() != null && weapon.getComponentOfType(ItemComponentMagazine.class).getLoadedAmmoType().getComponentOfType(ItemComponentAmmo.class).isReusable()) {
                getArea().getInventory().addItems(weapon.getComponentOfType(ItemComponentMagazine.class).getLoadedAmmoType().getTemplateID(), getAmmoConsumed());
            }
            weapon.getComponentOfType(ItemComponentMagazine.class).consumeAmmo(getAmmoConsumed());
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
        if (weapon != null && weapon.hasComponentOfType(ItemComponentMagazine.class) && weapon.getComponentOfType(ItemComponentMagazine.class).getAmmoRemaining() < getAmmoConsumed()) {
            return new CanChooseResult(false, "Not enough ammo");
        }
        if (!getRanges().contains(subject.getArea().getLinearDistanceTo(getArea()))) {
            return new CanChooseResult(false, "Target area outside range");
        }
        if (!getArea().isVisible(subject)) {
            return new CanChooseResult(false, "Target area not visible");
        }
        return new CanChooseResult(true, null);
    }

    @Override
    public MenuData getMenuData(Actor subject) {
        return new MenuDataAttackArea(getArea(), weapon);
    }

}
