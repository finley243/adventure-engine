package com.github.finley243.adventureengine.effect;

import com.github.finley243.adventureengine.actor.Actor;

import java.util.UUID;

/**
 * An effect that modifies an actor
 */
public abstract class Effect {

	protected final boolean manualRemoval;
	protected final int duration;

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

	public boolean needsSaveData() {
		return !manualRemoval;
	}

	@Override
	public boolean equals(Object o) {
		return getClass().equals(o.getClass()) && manualRemoval == ((Effect) o).manualRemoval && duration == ((Effect) o).duration;
	}

	@Override
	public int hashCode() {
		return (31 * (31 * getClass().getSimpleName().hashCode() + duration)) + Boolean.hashCode(manualRemoval);
	}
	
}
