package com.github.finley243.adventureengine.effect;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.GameInstanced;
import com.github.finley243.adventureengine.stat.EffectableStatHolder;

/**
 * An effect that modifies an actor
 */
public abstract class Effect extends GameInstanced {

	protected final boolean manualRemoval;
	protected final int duration;
	private final boolean stackable;

	public Effect(Game game, String ID, int duration, boolean manualRemoval, boolean stackable) {
		super(game, ID);
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
	
}
