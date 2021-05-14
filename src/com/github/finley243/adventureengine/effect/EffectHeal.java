package com.github.finley243.adventureengine.effect;

import com.github.finley243.adventureengine.actor.Actor;

public class EffectHeal extends Effect {

	private int amount;
	
	public EffectHeal(int amount) {
		super(0);
		this.amount = amount;
	}
	
	@Override
	protected void addEffect(Actor target) {
		target.heal(amount);
	}

}
