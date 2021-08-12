package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.menu.data.MenuData;
import com.github.finley243.adventureengine.menu.data.MenuDataEquipped;
import com.github.finley243.adventureengine.world.item.ItemWeapon;

public class ActionAttackRanged implements Action {
	
	private ItemWeapon weapon;
	private Actor target;
	
	public ActionAttackRanged(ItemWeapon weapon, Actor target) {
		this.weapon = weapon;
		this.target = target;
	}
	
	@Override
	public void choose(Actor subject) {
		weapon.attack(subject, target);
	}

	@Override
	public String getPrompt() {
		return "Attack " + target.getName() + " with " + weapon.getName();
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
		if(!(action instanceof ActionAttackRanged)) {
			return false;
		} else {
			return ((ActionAttackRanged) action).weapon == this.weapon;
		}
	}
	
	@Override
	public int actionCount() {
		return weapon.getRate();
	}
	
	@Override
	public ActionLegality getLegality() {
		return ActionLegality.HOSTILE;
	}
	
	@Override
	public MenuData getMenuData() {
		return new MenuDataEquipped("Attack " + target.getName(), weapon);
	}

	@Override
    public boolean equals(Object o) {
        if(!(o instanceof ActionAttackRanged)) {
            return false;
        } else {
            ActionAttackRanged other = (ActionAttackRanged) o;
            return other.weapon == this.weapon && other.target == this.target;
        }
    }
	
}
