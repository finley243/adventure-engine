package com.github.finley243.adventureengine.effect;

import com.github.finley243.adventureengine.actor.Actor;

/**
 * An effect that modifies an actor (modification can be temporary or permanent)
 */
public abstract class Effect {

	protected final boolean manualRemoval;

	private boolean hasAdded;
	protected int turnsRemaining;

	public Effect(int duration, boolean manualRemoval) {
		this.manualRemoval = manualRemoval;
		this.turnsRemaining = duration;
	}
	
	public void update(Actor target) {
		if (!hasAdded) {
			start(target);
			hasAdded = true;
		} else if (!manualRemoval) {
			if(turnsRemaining == 0) {
				end(target);
			}
			turnsRemaining--;
		}
		eachTurn(target);
	}
	
	public abstract void start(Actor target);
	
	public abstract void end(Actor target);
	
	public abstract void eachTurn(Actor target);
	
	public boolean shouldRemove() {
		return !manualRemoval && turnsRemaining < 0;
	}
	
	public abstract Effect generate();

	@Override
	public boolean equals(Object o) {
		return o == this;
	}
	
}
