package com.github.finley243.adventureengine.action.attack;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Limb;
import com.github.finley243.adventureengine.combat.WeaponAttackType;
import com.github.finley243.adventureengine.item.ItemWeapon;
import com.github.finley243.adventureengine.menu.MenuChoice;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.textgen.Noun;
import com.github.finley243.adventureengine.world.AttackTarget;
import com.github.finley243.adventureengine.world.environment.AreaLink;

import java.util.List;
import java.util.Set;

public class ActionAttackLimb extends ActionAttack {

	private final AttackTarget target;
	private final ItemWeapon weapon;

	public ActionAttackLimb(ItemWeapon weapon, AttackTarget target, Limb limb, String prompt, String hitPhrase, String hitPhraseRepeat, String hitOverallPhrase, String hitOverallPhraseRepeat, String hitPhraseAudible, String hitPhraseRepeatAudible, String hitOverallPhraseAudible, String hitOverallPhraseRepeatAudible, String missPhrase, String missPhraseRepeat, String missOverallPhrase, String missOverallPhraseRepeat, String missPhraseAudible, String missPhraseRepeatAudible, String missOverallPhraseAudible, String missOverallPhraseRepeatAudible, String skill, float baseHitChanceMin, float baseHitChanceMax, int ammoConsumed, WeaponAttackType.WeaponConsumeType weaponConsumeType, Set<AreaLink.DistanceCategory> ranges, int rate, int damage, String damageType, float armorMult, List<String> targetEffects, float hitChanceMult, boolean canDodge, AttackHitChanceType hitChanceType, boolean isLoud) {
		super(weapon, Set.of(target), limb, null, prompt, hitPhrase, hitPhraseRepeat, hitOverallPhrase, hitOverallPhraseRepeat, hitPhraseAudible, hitPhraseRepeatAudible, hitOverallPhraseAudible, hitOverallPhraseRepeatAudible, missPhrase, missPhraseRepeat, missOverallPhrase, missOverallPhraseRepeat, missPhraseAudible, missPhraseRepeatAudible, missOverallPhraseAudible, missOverallPhraseRepeatAudible, skill, baseHitChanceMin, baseHitChanceMax, ammoConsumed, weaponConsumeType, ranges, rate, damage, damageType, armorMult, targetEffects, hitChanceMult, canDodge, hitChanceType, isLoud);
		this.target = target;
		this.weapon = weapon;
	}

	@Override
	public void consumeAmmo(Actor subject) {
		if (weapon.usesAmmo() && weapon.getLoadedAmmoType() != null) {
			if (weapon.getLoadedAmmoType().isReusable()) {
				target.getArea().getInventory().addItems(weapon.getLoadedAmmoType().getTemplateID(), getAmmoConsumed());
			}
			weapon.consumeAmmo(getAmmoConsumed());
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
		if (weapon.usesAmmo() && weapon.getAmmoRemaining() < getAmmoConsumed()) {
			return new CanChooseResult(false, "Not enough ammo");
		}
		if (!getRanges().contains(subject.getArea().getDistanceTo(target.getArea().getID()))) {
			return new CanChooseResult(false, "Target outside range");
		}
		if (!subject.canSee(target)) {
			return new CanChooseResult(false, "Target not visible");
		}
		return new CanChooseResult(true, null);
	}
	
	@Override
	public MenuChoice getMenuChoices(Actor subject) {
		return new MenuChoice(LangUtils.titleCase(getLimb().getName()) + " (" + getChanceTag(subject) + ")",
				canChoose(subject).canChoose(), new String[]{"Attack", LangUtils.titleCase(weapon.getName()), LangUtils.titleCase(getPrompt()), LangUtils.titleCase(((Noun) target).getName())}, new String[]{getPrompt().toLowerCase() + " " + LangUtils.possessive(((Noun) target).getName(), false) + " " + getLimb().getName() + " with " + weapon.getName(), getPrompt().toLowerCase() + " at " + LangUtils.possessive(((Noun) target).getName(), false) + " " + getLimb().getName() + " with " + weapon.getName(), getPrompt().toLowerCase() + " " + weapon.getName() + " at " + LangUtils.possessive(((Noun) target).getName(), false) + " " + getLimb().getName(), getPrompt().toLowerCase() + " with " + weapon.getName() + " at " + LangUtils.possessive(((Noun) target).getName(), false) + " " + getLimb().getName()});
	}

}
