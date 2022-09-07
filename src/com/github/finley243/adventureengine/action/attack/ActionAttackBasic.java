package com.github.finley243.adventureengine.action.attack;

import com.github.finley243.adventureengine.Damage;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.CombatHelper;
import com.github.finley243.adventureengine.item.ItemWeapon;
import com.github.finley243.adventureengine.menu.MenuData;
import com.github.finley243.adventureengine.textgen.Noun;
import com.github.finley243.adventureengine.world.AttackTarget;
import com.github.finley243.adventureengine.world.environment.AreaLink;

import java.util.Set;

public class ActionAttackBasic extends ActionAttack {

	private final AttackTarget target;
	private final ItemWeapon weapon;

	public ActionAttackBasic(ItemWeapon weapon, AttackTarget target, String prompt, String hitPhrase, String hitPhraseRepeat, String missPhrase, String missPhraseRepeat, Actor.Skill skill, int ammoConsumed, Set<AreaLink.DistanceCategory> ranges, int rate, int damage, Damage.DamageType damageType, float armorMult, float hitChanceMult, boolean canDodge) {
		super(weapon, Set.of(target), null, prompt, hitPhrase, hitPhraseRepeat, missPhrase, missPhraseRepeat, skill, ammoConsumed, ranges, rate, damage, damageType, armorMult, hitChanceMult, canDodge);
		this.target = target;
		this.weapon = weapon;
	}

	@Override
	public float chance(Actor subject, AttackTarget target) {
		return CombatHelper.calculateHitChance(subject, target, getLimb(), getSkill(), weapon.getBaseHitChanceMin(), weapon.getBaseHitChanceMax(), weapon.getAccuracyBonus(), canDodge(), hitChanceMult());
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
				&& getRanges().contains(subject.getArea().getDistanceTo(target.getArea().getID()))
				&& subject.canSee(target);
	}
	
	@Override
	public MenuData getMenuData(Actor subject) {
		return new MenuData(getPrompt() + " (" + getChanceTag(subject) + ")", canChoose(subject), new String[]{"attack", weapon.getName(), ((Noun) target).getName()});
	}

}
