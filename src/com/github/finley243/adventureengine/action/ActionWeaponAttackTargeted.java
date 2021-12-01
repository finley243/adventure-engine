package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Limb;
import com.github.finley243.adventureengine.menu.MenuData;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.world.item.ItemWeapon;

public class ActionWeaponAttackTargeted extends ActionAttack {

	private final Limb limb;

	public ActionWeaponAttackTargeted(ItemWeapon weapon, Actor target, Limb limb) {
		super(weapon, target);
		this.limb = limb;
	}

	@Override
	public void choose(Actor subject) {
		getWeapon().attack(subject, getTarget(), limb);
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
		if(action instanceof ActionWeaponAttackTargeted) {
			return ((ActionWeaponAttackTargeted) action).getWeapon() == this.getWeapon() && ((ActionWeaponAttackTargeted) action).limb == this.limb;
		} else {
			return false;
		}
	}
	
	@Override
	public MenuData getMenuData(Actor subject) {
		return new MenuData("Targeted Attack (" + LangUtils.titleCase(getWeapon().getName()) + ", " + LangUtils.titleCase(limb.getName()) + ")", "Attack " + getTarget().getFormattedName(false) + " with " + getWeapon().getFormattedName(false) + " (targeted: " + limb.getName() + ")", canChoose(subject), new String[]{getTarget().getName()});
	}

}
