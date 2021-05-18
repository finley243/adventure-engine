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
		} else if(turnsRemaining == 0) {
			end(target);
		}
		eachTurn(target);
		turnsRemaining--;
	}
	
	protected void start(Actor target) {
		
	}
	
	protected void end(Actor target) {
		
	}
	
	protected void eachTurn(Actor target) {
		
	}
	
	public boolean shouldRemove() {
		return turnsRemaining < 0;
	}
	
	public abstract void apply(Actor target);
	
}
