package com.github.finley243.adventureengine.effect;

import com.github.finley243.adventureengine.actor.Actor;

/**
 * An effect that modifies an actor (modification can be temporary or permanent)
 */
public abstract class Effect {

	private boolean hasAdded;
	protected int turnsRemaining;
	
	public Effect(int duration) {
		this.turnsRemaining = duration;
	}
	
	public void update(Actor target) {
		if(!hasAdded) {
			start(target);
			hasAdded = true;
			if(turnsRemaining > 0) {
				turnsRemaining--;
			}
		} else if(turnsRemaining == 0) {
			end(target);
		} else {
			turnsRemaining--;
		}
		eachTurn(target);
	}
	
	protected void start(Actor target) {
		
	}
	
	protected void end(Actor target) {
		
	}
	
	protected void eachTurn(Actor target) {
		
	}
	
	public boolean shouldRemove() {
		return turnsRemaining == 0;
	}
	
	public void apply(Actor target) {
		
	}
	
}
