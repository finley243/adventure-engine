package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.world.AttackTarget;
import com.github.finley243.adventureengine.world.item.ItemWeapon;

public class ActionAttack implements Action {

	private ItemWeapon weapon;
	private AttackTarget target;
	
	public ActionAttack(ItemWeapon weapon, AttackTarget target) {
		this.weapon = weapon;
		this.target = target;
	}

	@Override
	public void choose(Actor subject) {
		
	}

	@Override
	public String getChoiceName() {
		return "Attack " + target.getName() + " with " + weapon.getName();
	}

	@Override
	public float utility(Actor subject) {
		return 0.0f;
	}
	
}
