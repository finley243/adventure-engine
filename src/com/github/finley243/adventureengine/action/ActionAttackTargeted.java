package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Limb;
import com.github.finley243.adventureengine.menu.data.MenuData;
import com.github.finley243.adventureengine.menu.data.MenuDataWorldActor;
import com.github.finley243.adventureengine.textgen.LangUtils;
import com.github.finley243.adventureengine.world.item.ItemWeapon;

public class ActionAttackTargeted implements Action {

	private boolean disabled;
	private final ItemWeapon weapon;
	private final Actor target;
	private final Limb limb;

	public ActionAttackTargeted(ItemWeapon weapon, Actor target, Limb limb) {
		this.weapon = weapon;
		this.target = target;
		this.limb = limb;
	}

	@Override
	public void choose(Actor subject) {
		weapon.attack(subject, target, limb);
	}

	@Override
	public boolean canChoose(Actor subject) {
		return !disabled;
	}

	@Override
	public void disable() {
		disabled = true;
	}

	@Override
	public float utility(Actor subject) {
		if (!subject.isCombatTarget(target)) return 0;
		return 0.8f;
	}
	
	@Override
	public boolean usesAction() {
		return true;
	}
	
	@Override
	public boolean canRepeat() {
		return false;
	}

	@Override
	public boolean isRepeatMatch(Action action) {
		if(!(action instanceof ActionAttackTargeted)) {
			return false;
		} else {
			return ((ActionAttackTargeted) action).weapon == this.weapon;
		}
	}
	
	@Override
	public int actionCount() {
		return weapon.getRate();
	}
	
	@Override
	public MenuData getMenuData(Actor subject) {
		return new MenuDataWorldActor("Targeted Attack (" + LangUtils.titleCase(weapon.getName()) + ", " + LangUtils.titleCase(limb.getName()) + ")", "Attack " + target.getFormattedName(false) + " with " + weapon.getFormattedName(false) + " (targeted: " + limb.getName() + ")", canChoose(subject), target);
	}
	
	@Override
    public boolean equals(Object o) {
        if(!(o instanceof ActionAttackTargeted)) {
            return false;
        } else {
            ActionAttackTargeted other = (ActionAttackTargeted) o;
            return other.weapon == this.weapon && other.target == this.target;
        }
    }

}
