package com.github.finley243.adventureengine.effect;

import com.github.finley243.adventureengine.actor.Actor;

/**
 * An effect that modifies an actor (modification can be temporary or permanent)
 */
public abstract class Effect {

	private boolean hasAdded;
	private int turnsRemaining;
	
	public Effect(int duration) {
		this.turnsRemaining = duration;
	}
	
	public void update(Actor target) {
		if(!hasAdded) {
			addEffect(target);
			hasAdded = true;
			if(turnsRemaining > 0) {
				turnsRemaining--;
			}
		} else if(turnsRemaining == 0) {
			removeEffect(target);
		} else {
			turnsRemaining--;
		}
	}
	
	protected void addEffect(Actor target) {
		
	}
	
	protected void removeEffect(Actor target) {
		
	}
	
	public boolean shouldRemove() {
		return turnsRemaining == 0;
	}
	
}
