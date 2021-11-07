package com.github.finley243.adventureengine.effect;

import com.github.finley243.adventureengine.actor.Actor;

public class EffectHeal extends Effect {

	private final int amount;
	
	public EffectHeal(int amount) {
		super(0);
		this.amount = amount;
	}
	
	@Override
	protected void start(Actor target) {
		target.heal(amount);
	}
	
	@Override
	public void apply(Actor target) {
		target.addEffect(new EffectHeal(amount));
	}

}
