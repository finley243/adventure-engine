package com.github.finley243.adventureengine.action.attack;

import com.github.finley243.adventureengine.action.ActionDependencies;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.combat.AttackType;
import com.github.finley243.adventureengine.combat.DamageType;
import com.github.finley243.adventureengine.effect.Effect;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.component.MagazineItemComponent;
import com.github.finley243.adventureengine.item.template.AmmoItemComponentTemplate;
import com.github.finley243.adventureengine.menu.action.MenuData;
import com.github.finley243.adventureengine.menu.action.MenuDataAttackArea;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.environment.AreaLink;

import java.util.List;
import java.util.Set;

public class ActionAttackArea extends ActionAttack {

    private final Item weapon;

    public ActionAttackArea(Actor subject, ActionDependencies dependencies, AttackType attackType, Item weapon, Area area, String prompt, String attackPhrase, String attackOverallPhrase, String attackPhraseAudible, String attackOverallPhraseAudible, int ammoConsumed, int actionPoints, AttackType.WeaponConsumeType weaponConsumeType, Set<AreaLink.DistanceCategory> ranges, int rate, Script damage, DamageType damageType, float armorMult, List<Effect> targetEffects, Script hitChanceExpression, Script hitChanceOverallExpression, float hitChanceMult, boolean isLoud, AreaLink.DistanceCategory targetDistance) {
        super(subject, dependencies, attackType, weapon, area.getAttackTargets(), null, area, prompt, attackPhrase, attackOverallPhrase, attackPhraseAudible, attackOverallPhraseAudible, ammoConsumed, actionPoints, weaponConsumeType, ranges, rate, damage, damageType, armorMult, targetEffects, hitChanceExpression, hitChanceOverallExpression, hitChanceMult, isLoud, targetDistance);
        this.weapon = weapon;
    }

    @Override
    public void consumeAmmo(Actor subject) {
        if (weapon == null) {
            return;
        }
        if (weapon.hasComponentOfType(MagazineItemComponent.class)) {
            if (weapon.getComponentOfType(MagazineItemComponent.class).getLoadedAmmoType() != null && weapon.getComponentOfType(MagazineItemComponent.class).getLoadedAmmoType().getComponentTemplate(AmmoItemComponentTemplate.class).isReusable()) {
                getArea().getInventory().addItems(weapon.getComponentOfType(MagazineItemComponent.class).getLoadedAmmoType().getID(), getAmmoConsumed());
            }
            weapon.getComponentOfType(MagazineItemComponent.class).consumeAmmo(getAmmoConsumed());
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
    public CanChooseResult canChoose() {
        CanChooseResult resultSuper = super.canChoose();
        if (!resultSuper.canChoose()) {
            return resultSuper;
        }
        if (weapon != null && weapon.hasComponentOfType(MagazineItemComponent.class) && weapon.getComponentOfType(MagazineItemComponent.class).getAmmoRemaining() < getAmmoConsumed()) {
            return new CanChooseResult(false, "Not enough ammo");
        }
        if (!getRanges().contains(getTargetDistance())) {
            return new CanChooseResult(false, "Target area outside range");
        }
        return new CanChooseResult(true, null);
    }

    @Override
    public MenuData getMenuData() {
        return new MenuDataAttackArea(getArea(), weapon);
    }

}
