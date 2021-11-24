package com.github.finley243.adventureengine.effect;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Actor.Attribute;

public class EffectAttribute extends Effect {

	private final Attribute attribute;
	private final int amount;
	
	public EffectAttribute(int duration, boolean manualRemoval, Attribute attribute, int amount) {
		super(duration, manualRemoval);
		this.attribute = attribute;
		this.amount = amount;
	}
	
	@Override
	public void start(Actor target) {
		target.adjustAttributeMod(attribute, amount);
	}
	
	@Override
	public void end(Actor target) {
		target.adjustAttributeMod(attribute, -amount);
	}

	@Override
	public void eachTurn(Actor target){}
	
	@Override
	public Effect generate() {
		return new EffectAttribute(this.turnsRemaining, manualRemoval, attribute, amount);
	}

}
