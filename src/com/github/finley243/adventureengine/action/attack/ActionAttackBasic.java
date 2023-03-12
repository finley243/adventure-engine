package com.github.finley243.adventureengine.action.attack;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.combat.Damage;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.ItemWeapon;
import com.github.finley243.adventureengine.menu.MenuChoice;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.textgen.Noun;
import com.github.finley243.adventureengine.world.AttackTarget;
import com.github.finley243.adventureengine.world.environment.AreaLink;

import java.util.List;
import java.util.Set;

public class ActionAttackBasic extends ActionAttack {

	private final AttackTarget target;
	private final Item weapon;

	public ActionAttackBasic(Item weapon, AttackTarget target, String prompt, String hitPhrase, String hitPhraseRepeat, String hitOverallPhrase, String hitOverallPhraseRepeat, String missPhrase, String missPhraseRepeat, String missOverallPhrase, String missOverallPhraseRepeat, Actor.Skill skill, float baseHitChanceMin, float baseHitChanceMax, float hitChanceBonus, int ammoConsumed, Set<AreaLink.DistanceCategory> ranges, int rate, int damage, Damage.DamageType damageType, float armorMult, List<String> targetEffects, float hitChanceMult, boolean canDodge, AttackHitChanceType hitChanceType) {
		super(weapon, Set.of(target), null, prompt, hitPhrase, hitPhraseRepeat, hitOverallPhrase, hitOverallPhraseRepeat, missPhrase, missPhraseRepeat, missOverallPhrase, missOverallPhraseRepeat, skill, baseHitChanceMin, baseHitChanceMax, hitChanceBonus, ammoConsumed, ranges, rate, damage, damageType, armorMult, targetEffects, hitChanceMult, canDodge, hitChanceType);
		this.target = target;
		this.weapon = weapon;
	}

	@Override
	public void consumeAmmo(Actor subject) {
		if (weapon instanceof ItemWeapon) {
			if (((ItemWeapon) weapon).getClipSize() > 0) {
				if (((ItemWeapon) weapon).getLoadedAmmoType() != null && ((ItemWeapon) weapon).getLoadedAmmoType().isReusable()) {
					target.getArea().getInventory().addItems(((ItemWeapon) weapon).getLoadedAmmoType(), getAmmoConsumed());
				}
				((ItemWeapon) weapon).consumeAmmo(getAmmoConsumed());
			}
		} else {
			subject.getInventory().removeItems(weapon, getAmmoConsumed());
			// TODO - Make this optional (e.g. do not place a grenade object after using a grenade)
			//Item.itemToObject(weapon.game(), weapon, getAmmoConsumed(), target.getArea());
			target.getArea().getInventory().addItems(weapon, getAmmoConsumed());
		}
	}

	@Override
	public boolean canChoose(Actor subject) {
		return super.canChoose(subject)
				&& (!(weapon instanceof ItemWeapon) || ((ItemWeapon) weapon).getClipSize() == 0 || ((ItemWeapon) weapon).getAmmoRemaining() >= getAmmoConsumed())
				&& getRanges().contains(subject.getArea().getDistanceTo(target.getArea().getID()))
				&& subject.canSee(target);
	}
	
	@Override
	public MenuChoice getMenuChoices(Actor subject) {
		return new MenuChoice(LangUtils.titleCase(((Noun) target).getName()) + " (" + getChanceTag(subject) + ")", canChoose(subject), new String[]{"attack", weapon.getName(), getPrompt()}, new String[]{getPrompt().toLowerCase() + " " + ((Noun) target).getName() + " with " + weapon.getName(), getPrompt().toLowerCase() + " at " + ((Noun) target).getName() + " with " + weapon.getName(), getPrompt().toLowerCase() + " " + weapon.getName() + " at " + ((Noun) target).getName(), getPrompt().toLowerCase() + " with " + weapon.getName() + " at " + ((Noun) target).getName()});
	}

}
