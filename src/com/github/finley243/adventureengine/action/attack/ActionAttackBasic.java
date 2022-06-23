package com.github.finley243.adventureengine.action.attack;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.menu.MenuData;
import com.github.finley243.adventureengine.item.ItemWeapon;

public class ActionAttackBasic extends ActionAttack {

	public ActionAttackBasic(ItemWeapon weapon, Actor target, String prompt, String hitPhrase, String missPhrase, int ammoConsumed, boolean overrideWeaponRate, float damageMult, float hitChanceMult) {
		super(weapon, target, null, prompt, hitPhrase, missPhrase, ammoConsumed, overrideWeaponRate, damageMult, hitChanceMult);
	}

	@Override
	public boolean isRepeatMatch(Action action) {
		if(action instanceof ActionAttackBasic) {
			return ((ActionAttackBasic) action).getWeapon() == this.getWeapon();
		} else {
			return false;
		}
	}
	
	@Override
	public MenuData getMenuData(Actor subject) {
		return new MenuData(getPrompt() + " (" + getChanceTag(subject) + ")", canChoose(subject), new String[]{getWeapon().getName(), getTarget().getName()});
	}

}
