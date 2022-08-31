package com.github.finley243.adventureengine.action.attack;

import com.github.finley243.adventureengine.Damage;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.CombatHelper;
import com.github.finley243.adventureengine.actor.Limb;
import com.github.finley243.adventureengine.item.ItemWeapon;
import com.github.finley243.adventureengine.menu.MenuData;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.textgen.Noun;
import com.github.finley243.adventureengine.world.AttackTarget;
import com.github.finley243.adventureengine.world.environment.AreaLink;
import com.google.common.collect.Iterables;

import java.util.Set;

public class ActionAttackLimb extends ActionAttack {

	private final Actor target;
	private final ItemWeapon weapon;

	public ActionAttackLimb(ItemWeapon weapon, Actor target, Limb limb, String prompt, String hitPhrase, String hitPhraseRepeat, String missPhrase, String missPhraseRepeat, int ammoConsumed, AreaLink.DistanceCategory range, int rate, int damage, Damage.DamageType damageType, float armorMult, float hitChanceMult, boolean canDodge) {
		super(weapon, Set.of(target), limb, prompt, hitPhrase, hitPhraseRepeat, missPhrase, missPhraseRepeat, ammoConsumed, range, rate, damage, damageType, armorMult, hitChanceMult, canDodge);
		this.target = target;
		this.weapon = weapon;
	}

	@Override
	public float chance(Actor subject, AttackTarget target) {
		return CombatHelper.calculateHitChance(subject, target, getLimb(), weapon, weapon.getBaseHitChanceMin(), weapon.getBaseHitChanceMax(), canDodge(), hitChanceMult());
	}

	@Override
	public void consumeAmmo() {
		if(weapon.getClipSize() > 0) {
			weapon.consumeAmmo(getAmmoConsumed());
		}
	}

	@Override
	public boolean canChoose(Actor subject) {
		return super.canChoose(subject)
				&& (weapon.getClipSize() == 0 || weapon.getAmmoRemaining() >= getAmmoConsumed())
				&& subject.getArea().getDistanceTo(target.getArea().getID()) == getRange()
				&& subject.canSee(target);
	}
	
	@Override
	public MenuData getMenuData(Actor subject) {
		return new MenuData(LangUtils.titleCase(getLimb().getName()) + " (" + getChanceTag(subject) + ")",
				canChoose(subject), new String[]{"attack", weapon.getName(), ((Noun) Iterables.getOnlyElement(getTargets())).getName(), getPrompt()});
	}

}
