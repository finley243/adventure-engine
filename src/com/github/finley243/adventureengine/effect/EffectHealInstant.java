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
	public Effect generate() {
		return new EffectHealInstant(amount);
	}

}
