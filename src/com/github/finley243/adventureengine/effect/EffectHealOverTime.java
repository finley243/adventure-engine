package com.github.finley243.adventureengine.effect;

import com.github.finley243.adventureengine.actor.Actor;

public class EffectHealOverTime extends Effect {

	private int amount;
	
	public EffectHealOverTime(int duration, int amount) {
		super(duration);
		this.amount = amount;
	}
	
	@Override
	protected void eachTurn(Actor target) {
		target.heal(amount);
	}
	
	@Override
	public void apply(Actor target) {
		target.addEffect(new EffectHealOverTime(this.turnsRemaining, amount));
	}

}
