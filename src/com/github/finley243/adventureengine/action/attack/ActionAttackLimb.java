package com.github.finley243.adventureengine.action.attack;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Limb;
import com.github.finley243.adventureengine.menu.MenuData;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.item.ItemWeapon;

public class ActionAttackLimb extends ActionAttack {


	public ActionAttackLimb(ItemWeapon weapon, Actor target, Limb limb, String prompt, String hitPhrase, String missPhrase, int ammoConsumed, boolean overrideWeaponRate, float damageMult, float hitChanceMult) {
		super(weapon, target, limb, prompt, hitPhrase, missPhrase, ammoConsumed, overrideWeaponRate, damageMult, hitChanceMult);
	}
	
	@Override
	public MenuData getMenuData(Actor subject) {
		return new MenuData(LangUtils.titleCase(getLimb().getName()) + " (" + getChanceTag(subject) + ")",
				canChoose(subject), new String[]{getWeapon().getName(), getTarget().getName(), getPrompt()});
	}

}
