package com.github.finley243.adventureengine.actor;

public class CombatTarget {

	/** Number of turns it takes for a combat target to be removed if they are not visible */
	private static final int TURNS_BEFORE_END_COMBAT = 5;
	
	private Actor targetActor;
	private int turnsUntilRemove;
	
	public CombatTarget(Actor actor) {
		this.targetActor = actor;
		this.turnsUntilRemove = TURNS_BEFORE_END_COMBAT;
	}
	
	public void update(boolean isVisible) {
		if(isVisible) {
			turnsUntilRemove = TURNS_BEFORE_END_COMBAT;
		} else {
			turnsUntilRemove--;
		}
	}
	
	public boolean shouldRemove() {
		return turnsUntilRemove <= 0;
	}
	
	public Actor getTargetActor() {
		return targetActor;
	}
	
	public boolean shouldPursue(Actor subject) {
		return true;
	}
	
	@Override
	public boolean equals(Object other) {
		if(!(other instanceof CombatTarget)) {
			return false;
		} else {
			return this.getTargetActor().equals(((CombatTarget) other).getTargetActor());
		}
	}
	
	@Override
	public int hashCode() {
		return getTargetActor().hashCode();
	}
	
}
