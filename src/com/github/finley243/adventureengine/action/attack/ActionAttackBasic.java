package com.github.finley243.adventureengine.action.attack;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.menu.MenuData;
import com.github.finley243.adventureengine.item.ItemWeapon;

public class ActionAttackBasic extends ActionAttack {

	public ActionAttackBasic(ItemWeapon weapon, Actor target, String prompt, String hitPhrase, String hitPhraseRepeat, String missPhrase, String missPhraseRepeat, int ammoConsumed, boolean overrideWeaponRate, float damageMult, float hitChanceMult) {
		super(weapon, target, null, prompt, hitPhrase, hitPhraseRepeat, missPhrase, missPhraseRepeat, ammoConsumed, overrideWeaponRate, damageMult, hitChanceMult);
	}
	
	@Override
	public MenuData getMenuData(Actor subject) {
		return new MenuData(getPrompt() + " (" + getChanceTag(subject) + ")", canChoose(subject), new String[]{getWeapon().getName(), getTarget().getName()});
	}

}
