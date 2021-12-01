package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.menu.MenuData;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.world.item.ItemWeapon;

public class ActionWeaponAttack extends ActionAttack {
	
	public ActionWeaponAttack(ItemWeapon weapon, Actor target) {
		super(weapon, target);
	}

	@Override
	public void choose(Actor subject) {
		getWeapon().attack(subject, getTarget(), null);
	}

	@Override
	public float utility(Actor subject) {
		if (!subject.isCombatTarget(getTarget())) return 0;
		return 0.8f;
	}
	
	@Override
	public int multiCount() {
		return getWeapon().getRate();
	}

	@Override
	public boolean isMultiMatch(Action action) {
		if(action instanceof ActionWeaponAttack) {
			return ((ActionWeaponAttack) action).getWeapon() == this.getWeapon();
		} else {
			return false;
		}
	}
	
	@Override
	public MenuData getMenuData(Actor subject) {
		return new MenuData("Attack (" + LangUtils.titleCase(getWeapon().getName()) + ")", "Attack " + getTarget().getFormattedName(false) + " with " + getWeapon().getFormattedName(false), canChoose(subject), new String[]{getTarget().getName()});
	}

}
