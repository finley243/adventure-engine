package com.github.finley243.adventureengine.effect;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Actor.Attribute;

public class EffectAttribute extends Effect {

	private final Attribute attribute;

	public EffectAttribute(int duration, boolean manualRemoval, Attribute attribute, int amount) {
		super(duration, manualRemoval, amount);
		this.attribute = attribute;
	}
	
	@Override
	public void start(Actor target) {
		target.getAttribute(attribute).addMod(amount);
	}
	
	@Override
	public void end(Actor target) {
		target.getAttribute(attribute).addMod(-amount);
	}

	@Override
	public void eachTurn(Actor target){}

	@Override
	public boolean equals(Object o) {
		return super.equals(o) && attribute == ((EffectAttribute) o).attribute;
	}

	@Override
	public int hashCode() {
		return 31 * super.hashCode() + attribute.hashCode();
	}

}
