package com.github.finley243.adventureengine.action.attack;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Limb;
import com.github.finley243.adventureengine.combat.Damage;
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

	public ActionAttackLimb(ItemWeapon weapon, AttackTarget target, Limb limb, String prompt, String hitPhrase, String hitPhraseRepeat, String hitOverallPhrase, String hitOverallPhraseRepeat, String missPhrase, String missPhraseRepeat, String missOverallPhrase, String missOverallPhraseRepeat, Actor.Skill skill, float baseHitChanceMin, float baseHitChanceMax, float hitChanceBonus, int ammoConsumed, WeaponAttackType.WeaponConsumeType weaponConsumeType, Set<AreaLink.DistanceCategory> ranges, int rate, int damage, Damage.DamageType damageType, float armorMult, List<String> targetEffects, float hitChanceMult, boolean canDodge, AttackHitChanceType hitChanceType) {
		super(weapon, Set.of(target), limb, null, prompt, hitPhrase, hitPhraseRepeat, hitOverallPhrase, hitOverallPhraseRepeat, missPhrase, missPhraseRepeat, missOverallPhrase, missOverallPhraseRepeat, skill, baseHitChanceMin, baseHitChanceMax, hitChanceBonus, ammoConsumed, weaponConsumeType, ranges, rate, damage, damageType, armorMult, targetEffects, hitChanceMult, canDodge, hitChanceType);
		this.target = target;
		this.weapon = weapon;
	}

	@Override
	public void consumeAmmo(Actor subject) {
		if (weapon.getClipSize() > 0 && weapon.getLoadedAmmoType() != null) {
			if (weapon.getLoadedAmmoType().isReusable()) {
				target.getArea().getInventory().addItems(weapon.getLoadedAmmoType().getTemplate().getID(), getAmmoConsumed());
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
	public boolean canChoose(Actor subject) {
		return super.canChoose(subject)
				&& (weapon.getClipSize() == 0 || weapon.getAmmoRemaining() >= getAmmoConsumed())
				&& getRanges().contains(subject.getArea().getDistanceTo(target.getArea().getID()))
				&& subject.canSee(target);
	}
	
	@Override
	public MenuChoice getMenuChoices(Actor subject) {
		return new MenuChoice(LangUtils.titleCase(getLimb().getName()) + " (" + getChanceTag(subject) + ")",
				canChoose(subject), new String[]{"attack", weapon.getName(), getPrompt(), ((Noun) target).getName()}, new String[]{getPrompt().toLowerCase() + " " + LangUtils.possessive(((Noun) target).getName(), false) + " " + getLimb().getName() + " with " + weapon.getName(), getPrompt().toLowerCase() + " at " + LangUtils.possessive(((Noun) target).getName(), false) + " " + getLimb().getName() + " with " + weapon.getName(), getPrompt().toLowerCase() + " " + weapon.getName() + " at " + LangUtils.possessive(((Noun) target).getName(), false) + " " + getLimb().getName(), getPrompt().toLowerCase() + " with " + weapon.getName() + " at " + LangUtils.possessive(((Noun) target).getName(), false) + " " + getLimb().getName()});
	}

}
