package com.github.finley243.adventureengine.action.attack;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.combat.WeaponAttackType;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.component.ItemComponentAmmo;
import com.github.finley243.adventureengine.item.component.ItemComponentMagazine;
import com.github.finley243.adventureengine.menu.action.MenuData;
import com.github.finley243.adventureengine.menu.action.MenuDataAttack;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.world.AttackTarget;
import com.github.finley243.adventureengine.world.environment.AreaLink;

import java.util.List;
import java.util.Set;

public class ActionAttackBasic extends ActionAttack {

	private final AttackTarget target;
	private final Item weapon;

	public ActionAttackBasic(WeaponAttackType attackType, Item weapon, AttackTarget target, String prompt, String attackPhrase, String attackOverallPhrase, String attackPhraseAudible, String attackOverallPhraseAudible, int ammoConsumed, int actionPoints, WeaponAttackType.WeaponConsumeType weaponConsumeType, Set<AreaLink.DistanceCategory> ranges, int rate, Script damage, String damageType, float armorMult, List<String> targetEffects, Script hitChanceExpression, Script hitChanceOverallExpression, float hitChanceMult, boolean isLoud) {
		super(attackType, weapon, Set.of(target), null, null, prompt, attackPhrase, attackOverallPhrase, attackPhraseAudible, attackOverallPhraseAudible, ammoConsumed, actionPoints, weaponConsumeType, ranges, rate, damage, damageType, armorMult, targetEffects, hitChanceExpression, hitChanceOverallExpression, hitChanceMult, isLoud);
		this.target = target;
		this.weapon = weapon;
	}

	@Override
	public void consumeAmmo(Game game, Actor subject) {
		if (weapon == null) {
			return;
		}
		if (weapon.hasComponentOfType(ItemComponentMagazine.class)) {
			if (weapon.getComponentOfType(ItemComponentMagazine.class).getLoadedAmmoType() != null && weapon.getComponentOfType(ItemComponentMagazine.class).getLoadedAmmoType().getComponentOfType(ItemComponentAmmo.class).isReusable()) {
				target.getArea().getInventory().addItems(weapon.getComponentOfType(ItemComponentMagazine.class).getLoadedAmmoType().getTemplateID(), getAmmoConsumed(), game);
			}
			weapon.getComponentOfType(ItemComponentMagazine.class).consumeAmmo(game, getAmmoConsumed());
		}
		switch (getWeaponConsumeType()) {
			case PLACE -> {
				subject.getInventory().removeItem(weapon, game);
				target.getArea().getInventory().addItem(weapon, game);
			}
			case DESTROY -> subject.getInventory().removeItem(weapon, game);
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
		if (!getRanges().contains(subject.getArea().getLinearDistanceTo(game, target.getArea()))) {
			return new CanChooseResult(false, "Target outside range");
		}
		if (!target.isVisible(subject)) {
			return new CanChooseResult(false, "Target not visible");
		}
		return new CanChooseResult(true, null);
	}

	@Override
	public MenuData getMenuData(Actor subject) {
		return new MenuDataAttack(target, weapon);
	}

}
