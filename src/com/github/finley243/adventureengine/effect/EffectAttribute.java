package com.github.finley243.adventureengine.effect;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Actor.Attribute;

public class EffectAttribute extends Effect {

	private Attribute attribute;
	private int amount;
	
	public EffectAttribute(int duration, Attribute attribute, int amount) {
		super(duration);
		this.attribute = attribute;
		this.amount = amount;
	}
	
	@Override
	protected void start(Actor target) {
		target.adjustAttributeMod(attribute, amount);
	}
	
	@Override
	protected void end(Actor target) {
		target.adjustAttributeMod(attribute, -amount);
	}
	
	@Override
	public void apply(Actor target) {
		target.addEffect(new EffectAttribute(this.turnsRemaining, attribute, amount));
	}

}
