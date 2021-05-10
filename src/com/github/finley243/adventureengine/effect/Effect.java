package com.github.finley243.adventureengine.effect;

import com.github.finley243.adventureengine.actor.Actor;

/**
 * An effect that modifies an actor (modification can be temporary or permanent)
 */
public interface Effect {

	public void update(Actor target);
	
	public boolean isInstant();
	
}
