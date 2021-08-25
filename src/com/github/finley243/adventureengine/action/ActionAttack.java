package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.menu.data.MenuData;
import com.github.finley243.adventureengine.menu.data.MenuDataEquipped;
import com.github.finley243.adventureengine.world.item.ItemWeapon;

public class ActionAttack implements Action {
	
	private ItemWeapon weapon;
	private Actor target;
	
	public ActionAttack(ItemWeapon weapon, Actor target) {
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
		if(!(action instanceof ActionAttack)) {
			return false;
		} else {
			return ((ActionAttack) action).weapon == this.weapon;
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
        if(!(o instanceof ActionAttack)) {
            return false;
        } else {
            ActionAttack other = (ActionAttack) o;
            return other.weapon == this.weapon && other.target == this.target;
        }
    }

}
