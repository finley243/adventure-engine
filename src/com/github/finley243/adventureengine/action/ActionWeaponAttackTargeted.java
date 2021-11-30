package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Limb;
import com.github.finley243.adventureengine.menu.MenuData;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.world.item.ItemWeapon;

public class ActionWeaponAttackTargeted extends Action {

	private final ItemWeapon weapon;
	private final Actor target;
	private final Limb limb;

	public ActionWeaponAttackTargeted(ItemWeapon weapon, Actor target, Limb limb) {
		this.weapon = weapon;
		this.target = target;
		this.limb = limb;
	}

	@Override
	public void choose(Actor subject) {
		weapon.attack(subject, target, limb);
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
		if(!(action instanceof ActionWeaponAttackTargeted)) {
			return false;
		} else {
			return ((ActionWeaponAttackTargeted) action).weapon == this.weapon;
		}
	}
	
	@Override
	public int actionCount() {
		return weapon.getRate();
	}
	
	@Override
	public MenuData getMenuData(Actor subject) {
		return new MenuData("Targeted Attack (" + LangUtils.titleCase(weapon.getName()) + ", " + LangUtils.titleCase(limb.getName()) + ")", "Attack " + target.getFormattedName(false) + " with " + weapon.getFormattedName(false) + " (targeted: " + limb.getName() + ")", canChoose(subject), new String[]{target.getName()});
	}
	
	@Override
    public boolean equals(Object o) {
        if(!(o instanceof ActionWeaponAttackTargeted)) {
            return false;
        } else {
            ActionWeaponAttackTargeted other = (ActionWeaponAttackTargeted) o;
            return other.weapon == this.weapon && other.target == this.target;
        }
    }

}