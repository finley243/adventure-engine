package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.menu.MenuData;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.world.item.ItemWeapon;

public class ActionWeaponAttack extends Action {

	private final ItemWeapon weapon;
	private final Actor target;
	
	public ActionWeaponAttack(ItemWeapon weapon, Actor target) {
		this.weapon = weapon;
		this.target = target;
	}

	@Override
	public void choose(Actor subject) {
		weapon.attack(subject, target, null);
	}

	@Override
	public float utility(Actor subject) {
		if (!subject.isCombatTarget(target)) return 0;
		return 0.8f;
	}
	
	@Override
	public boolean canRepeat() {
		return false;
	}

	@Override
	public boolean isRepeatMatch(Action action) {
		if(!(action instanceof ActionWeaponAttack)) {
			return false;
		} else {
			return ((ActionWeaponAttack) action).weapon == this.weapon;
		}
	}
	
	@Override
	public int actionCount() {
		return weapon.getRate();
	}
	
	@Override
	public MenuData getMenuData(Actor subject) {
		return new MenuData("Attack (" + LangUtils.titleCase(weapon.getName()) + ")", "Attack " + target.getFormattedName(false) + " with " + weapon.getFormattedName(false), canChoose(subject), new String[]{target.getName()});
	}
	
	@Override
    public boolean equals(Object o) {
        if(!(o instanceof ActionWeaponAttack)) {
            return false;
        } else {
            ActionWeaponAttack other = (ActionWeaponAttack) o;
            return other.weapon == this.weapon && other.target == this.target;
        }
    }

}
