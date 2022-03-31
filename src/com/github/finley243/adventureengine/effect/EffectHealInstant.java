package com.github.finley243.adventureengine.effect;

import com.github.finley243.adventureengine.actor.Actor;

public class EffectHealInstant extends Effect {

	private final int amount;
	
	public EffectHealInstant(int amount) {
		super(0, false);
		this.amount = amount;
	}
	
	@Override
	public void start(Actor target) {
		target.heal(amount);
	}

	@Override
	public void end(Actor target) {}

	@Override
	public void eachTurn(Actor target){}

	@Override
	public boolean equals(Object o) {
		return super.equals(o) && amount == ((EffectHealInstant) o).amount;
	}

	@Override
	public int hashCode() {
		return (31 * super.hashCode()) + amount;
	}

}
