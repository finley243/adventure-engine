package com.github.finley243.adventureengine.effect;

import com.github.finley243.adventureengine.actor.Actor;

public class EffectHealOverTime extends Effect {

	private final int amount;
	
	public EffectHealOverTime(int duration, boolean manualRemoval, int amount) {
		super(duration, manualRemoval);
		this.amount = amount;
	}

	@Override
	public void start(Actor target){}

	@Override
	public void end(Actor target){}
	
	@Override
	public void eachTurn(Actor target) {
		target.heal(amount);
	}
	
	@Override
	public Effect generate() {
		return new EffectHealOverTime(this.turnsRemaining, manualRemoval, amount);
	}

}
