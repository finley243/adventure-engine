package com.github.finley243.adventureengine.action.attack;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.combat.WeaponAttackType;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.component.ItemComponentAmmo;
import com.github.finley243.adventureengine.item.component.ItemComponentWeapon;
import com.github.finley243.adventureengine.menu.action.MenuData;
import com.github.finley243.adventureengine.menu.action.MenuDataAttack;
import com.github.finley243.adventureengine.world.AttackTarget;
import com.github.finley243.adventureengine.world.environment.AreaLink;

import java.util.List;
import java.util.Set;

public class ActionAttackBasic extends ActionAttack {

	private final AttackTarget target;
	private final Item weapon;

	public ActionAttackBasic(WeaponAttackType attackType, Item weapon, AttackTarget target, String prompt, String hitPhrase, String hitPhraseRepeat, String hitOverallPhrase, String hitOverallPhraseRepeat, String hitPhraseAudible, String hitPhraseRepeatAudible, String hitOverallPhraseAudible, String hitOverallPhraseRepeatAudible, String missPhrase, String missPhraseRepeat, String missOverallPhrase, String missOverallPhraseRepeat, String missPhraseAudible, String missPhraseRepeatAudible, String missOverallPhraseAudible, String missOverallPhraseRepeatAudible, String skill, float baseHitChanceMin, float baseHitChanceMax, int ammoConsumed, int actionPoints, WeaponAttackType.WeaponConsumeType weaponConsumeType, Set<AreaLink.DistanceCategory> ranges, int rate, int damage, String damageType, float armorMult, List<String> targetEffects, float hitChanceMult, String dodgeSkill, AttackHitChanceType hitChanceType, boolean isLoud) {
		super(attackType, weapon, Set.of(target), null, null, prompt, hitPhrase, hitPhraseRepeat, hitOverallPhrase, hitOverallPhraseRepeat, hitPhraseAudible, hitPhraseRepeatAudible, hitOverallPhraseAudible, hitOverallPhraseRepeatAudible, missPhrase, missPhraseRepeat, missOverallPhrase, missOverallPhraseRepeat, missPhraseAudible, missPhraseRepeatAudible, missOverallPhraseAudible, missOverallPhraseRepeatAudible, skill, baseHitChanceMin, baseHitChanceMax, ammoConsumed, actionPoints, weaponConsumeType, ranges, rate, damage, damageType, armorMult, targetEffects, hitChanceMult, dodgeSkill, hitChanceType, isLoud);
		this.target = target;
		this.weapon = weapon;
	}

	@Override
	public void consumeAmmo(Actor subject) {
		if (weapon.getComponentOfType(ItemComponentWeapon.class).usesAmmo()) {
			if (weapon.getComponentOfType(ItemComponentWeapon.class).getLoadedAmmoType() != null && weapon.getComponentOfType(ItemComponentWeapon.class).getLoadedAmmoType().getComponentOfType(ItemComponentAmmo.class).isReusable()) {
				target.getArea().getInventory().addItems(weapon.getComponentOfType(ItemComponentWeapon.class).getLoadedAmmoType().getTemplateID(), getAmmoConsumed());
			}
			weapon.getComponentOfType(ItemComponentWeapon.class).consumeAmmo(getAmmoConsumed());
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
		if (weapon.getComponentOfType(ItemComponentWeapon.class).usesAmmo() && weapon.getComponentOfType(ItemComponentWeapon.class).getAmmoRemaining() < getAmmoConsumed()) {
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
		return new MenuDataAttack(target, weapon);
	}

}
