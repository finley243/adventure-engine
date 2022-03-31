package com.github.finley243.adventureengine.effect;

import com.github.finley243.adventureengine.actor.Actor;

public class EffectHeal extends Effect {

	private final int amount;
	
	public EffectHeal(int duration, boolean manualRemoval, int amount) {
		super(duration, manualRemoval);
		this.amount = amount;
	}

	@Override
	public void start(Actor target){
		target.heal(amount);
	}

	@Override
	public void end(Actor target){}
	
	@Override
	public void eachTurn(Actor target) {
		target.heal(amount);
	}

	@Override
	public boolean equals(Object o) {
		return super.equals(o) && amount == ((EffectHeal) o).amount;
	}

	@Override
	public int hashCode() {
		return (31 * super.hashCode()) + amount;
	}

}
