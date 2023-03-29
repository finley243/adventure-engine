package com.github.finley243.adventureengine.effect;

import com.github.finley243.adventureengine.stat.EffectableStatHolder;

/**
 * An effect that modifies an actor
 */
public abstract class Effect {

	protected final boolean manualRemoval;
	protected final int duration;
	private final boolean stackable;

	public Effect(int duration, boolean manualRemoval, boolean stackable) {
		this.manualRemoval = manualRemoval;
		this.duration = duration;
		this.stackable = stackable;
	}
	
	public abstract void start(EffectableStatHolder target);
	
	public abstract void end(EffectableStatHolder target);
	
	public abstract void eachRound(EffectableStatHolder target);

	public boolean manualRemoval() {
		return manualRemoval;
	}

	public int getDuration() {
		return duration;
	}

	public boolean isStackable() {
		return stackable;
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
