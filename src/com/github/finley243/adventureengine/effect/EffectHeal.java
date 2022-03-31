package com.github.finley243.adventureengine.effect;

import com.github.finley243.adventureengine.actor.Actor;

public class EffectHeal extends Effect {

	public EffectHeal(int duration, boolean manualRemoval, int amount) {
		super(duration, manualRemoval, amount);
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

}
