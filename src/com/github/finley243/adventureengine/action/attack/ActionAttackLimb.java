package com.github.finley243.adventureengine.action.attack;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Limb;
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

public class ActionAttackLimb extends ActionAttack {

	private final AttackTarget target;
	private final Item weapon;

	public ActionAttackLimb(Item weapon, AttackTarget target, Limb limb, String prompt, String hitPhrase, String hitPhraseRepeat, String hitOverallPhrase, String hitOverallPhraseRepeat, String missPhrase, String missPhraseRepeat, String missOverallPhrase, String missOverallPhraseRepeat, Actor.Skill skill, float baseHitChanceMin, float baseHitChanceMax, float hitChanceBonus, int ammoConsumed, Set<AreaLink.DistanceCategory> ranges, int rate, int damage, Damage.DamageType damageType, float armorMult, List<String> targetEffects, float hitChanceMult, boolean canDodge, AttackHitChanceType hitChanceType) {
		super(weapon, Set.of(target), limb, prompt, hitPhrase, hitPhraseRepeat, hitOverallPhrase, hitOverallPhraseRepeat, missPhrase, missPhraseRepeat, missOverallPhrase, missOverallPhraseRepeat, skill, baseHitChanceMin, baseHitChanceMax, hitChanceBonus, ammoConsumed, ranges, rate, damage, damageType, armorMult, targetEffects, hitChanceMult, canDodge, hitChanceType);
		this.target = target;
		this.weapon = weapon;
	}

	@Override
	public void consumeAmmo(Actor subject) {
		if (weapon instanceof ItemWeapon) {
			if (((ItemWeapon) weapon).getClipSize() > 0 && ((ItemWeapon) weapon).getLoadedAmmoType() != null) {
				if (((ItemWeapon) weapon).getLoadedAmmoType().isReusable()) {
					target.getArea().getInventory().addItems(((ItemWeapon) weapon).getLoadedAmmoType().getTemplate().getID(), getAmmoConsumed());
				}
				((ItemWeapon) weapon).consumeAmmo(getAmmoConsumed());
			}
		} else {
			subject.getInventory().removeItems(weapon.getTemplate().getID(), getAmmoConsumed());
			// TODO - Make this optional (e.g. do not place a grenade object after using a grenade)
			target.getArea().getInventory().addItems(weapon.getTemplate().getID(), getAmmoConsumed());
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
		return new MenuChoice(LangUtils.titleCase(getLimb().getName()) + " (" + getChanceTag(subject) + ")",
				canChoose(subject), new String[]{"attack", weapon.getName(), getPrompt(), ((Noun) target).getName()}, new String[]{getPrompt().toLowerCase() + " " + LangUtils.possessive(((Noun) target).getName(), false) + " " + getLimb().getName() + " with " + weapon.getName(), getPrompt().toLowerCase() + " at " + LangUtils.possessive(((Noun) target).getName(), false) + " " + getLimb().getName() + " with " + weapon.getName(), getPrompt().toLowerCase() + " " + weapon.getName() + " at " + LangUtils.possessive(((Noun) target).getName(), false) + " " + getLimb().getName(), getPrompt().toLowerCase() + " with " + weapon.getName() + " at " + LangUtils.possessive(((Noun) target).getName(), false) + " " + getLimb().getName()});
	}

}
