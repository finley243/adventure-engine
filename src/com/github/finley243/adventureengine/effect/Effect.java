package com.github.finley243.adventureengine.effect;

import com.github.finley243.adventureengine.actor.Actor;

import java.util.UUID;

/**
 * An effect that modifies an actor
 */
public abstract class Effect {

	protected final boolean manualRemoval;
	private final int duration;

	public Effect(int duration, boolean manualRemoval) {
		this.manualRemoval = manualRemoval;
		this.duration = duration;
	}
	
	public abstract void start(Actor target);
	
	public abstract void end(Actor target);
	
	public abstract void eachTurn(Actor target);

	public boolean manualRemoval() {
		return manualRemoval;
	}

	public int getDuration() {
		return duration;
	}

	public boolean isInstant() {
		return !manualRemoval && duration == 0;
	}

	@Override
	public boolean equals(Object o) {
		return o == this;
	}
	
}
