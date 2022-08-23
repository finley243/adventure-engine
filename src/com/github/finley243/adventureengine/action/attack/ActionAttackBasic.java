package com.github.finley243.adventureengine.action.attack;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.CombatHelper;
import com.github.finley243.adventureengine.item.ItemWeapon;
import com.github.finley243.adventureengine.menu.MenuData;
import com.github.finley243.adventureengine.textgen.Noun;
import com.github.finley243.adventureengine.world.AttackTarget;
import com.google.common.collect.Iterables;

import java.util.Set;

public class ActionAttackBasic extends ActionAttack {

	private final Actor target;

	public ActionAttackBasic(ItemWeapon weapon, Actor target, String prompt, String hitPhrase, String hitPhraseRepeat, String missPhrase, String missPhraseRepeat, int ammoConsumed, boolean overrideWeaponRate, float damageMult, float hitChanceMult) {
		super(weapon, Set.of(target), null, prompt, hitPhrase, hitPhraseRepeat, missPhrase, missPhraseRepeat, ammoConsumed, overrideWeaponRate, damageMult, hitChanceMult, !weapon.isRanged());
		this.target = target;
	}

	@Override
	public float chance(Actor subject, AttackTarget target) {
		return CombatHelper.calculateHitChance(subject, target, getLimb(), getWeapon(), getWeapon().getBaseHitChanceMin(), getWeapon().getBaseHitChanceMax(), !getWeapon().isRanged(), hitChanceMult());
	}

	@Override
	public boolean canChoose(Actor subject) {
		return super.canChoose(subject) && subject.getArea().getDistanceTo(target.getArea().getID()) == getWeapon().getRange() && subject.canSee(target);
	}
	
	@Override
	public MenuData getMenuData(Actor subject) {
		return new MenuData(getPrompt() + " (" + getChanceTag(subject) + ")", canChoose(subject), new String[]{"attack", getWeapon().getName(), ((Noun) Iterables.getOnlyElement(getTargets())).getName()});
	}

}
