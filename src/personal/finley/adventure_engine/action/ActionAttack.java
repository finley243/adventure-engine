package personal.finley.adventure_engine.action;

import personal.finley.adventure_engine.actor.Actor;
import personal.finley.adventure_engine.world.IAttackTarget;
import personal.finley.adventure_engine.world.item.ItemWeapon;

public class ActionAttack implements IAction {

	private ItemWeapon weapon;
	private IAttackTarget target;
	
	public ActionAttack(ItemWeapon weapon, IAttackTarget target) {
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
