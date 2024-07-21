package com.github.finley243.adventureengine.action.attack;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Limb;
import com.github.finley243.adventureengine.combat.WeaponAttackType;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.component.ItemComponentAmmo;
import com.github.finley243.adventureengine.item.component.ItemComponentMagazine;
import com.github.finley243.adventureengine.menu.action.MenuData;
import com.github.finley243.adventureengine.menu.action.MenuDataAttackTargeted;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.world.AttackTarget;
import com.github.finley243.adventureengine.world.environment.AreaLink;

import java.util.List;
import java.util.Set;

public class ActionAttackLimb extends ActionAttack {

	private final AttackTarget target;
	private final Item weapon;

	public ActionAttackLimb(WeaponAttackType attackType, Item weapon, AttackTarget target, Limb limb, String prompt, String attackPhrase, String attackOverallPhrase, String attackPhraseAudible, String attackOverallPhraseAudible, String skill, float baseHitChanceMin, float baseHitChanceMax, int ammoConsumed, int actionPoints, WeaponAttackType.WeaponConsumeType weaponConsumeType, Set<AreaLink.DistanceCategory> ranges, int rate, Script damage, String damageType, float armorMult, List<String> targetEffects, float hitChanceMult, String dodgeSkill, AttackHitChanceType hitChanceType, boolean isLoud) {
		super(attackType, weapon, Set.of(target), limb, null, prompt, attackPhrase, attackOverallPhrase, attackPhraseAudible, attackOverallPhraseAudible, skill, baseHitChanceMin, baseHitChanceMax, ammoConsumed, actionPoints, weaponConsumeType, ranges, rate, damage, damageType, armorMult, targetEffects, hitChanceMult, dodgeSkill, hitChanceType, isLoud);
		this.target = target;
		this.weapon = weapon;
	}

	@Override
	public void consumeAmmo(Actor subject) {
		if (weapon == null) {
			return;
		}
		if (weapon.hasComponentOfType(ItemComponentMagazine.class)) {
			if (weapon.getComponentOfType(ItemComponentMagazine.class).getLoadedAmmoType() != null && weapon.getComponentOfType(ItemComponentMagazine.class).getLoadedAmmoType().getComponentOfType(ItemComponentAmmo.class).isReusable()) {
				target.getArea().getInventory().addItems(weapon.getComponentOfType(ItemComponentMagazine.class).getLoadedAmmoType().getTemplateID(), getAmmoConsumed());
			}
			weapon.getComponentOfType(ItemComponentMagazine.class).consumeAmmo(getAmmoConsumed());
		}
		switch (getWeaponConsumeType()) {
			case PLACE -> {
				subject.getInventory().removeItem(weapon);
				target.getArea().getInventory().addItem(weapon);
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
		if (!getRanges().contains(subject.getArea().getLinearDistanceTo(target.getArea()))) {
			return new CanChooseResult(false, "Target outside range");
		}
		if (!target.isVisible(subject)) {
			return new CanChooseResult(false, "Target not visible");
		}
		return new CanChooseResult(true, null);
	}

	@Override
	public MenuData getMenuData(Actor subject) {
		return new MenuDataAttackTargeted(target, getLimb(), weapon);
	}

}
