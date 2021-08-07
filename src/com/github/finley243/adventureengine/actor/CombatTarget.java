package com.github.finley243.adventureengine.actor;

public class CombatTarget {

	/** Number of turns it takes for a combat target to be removed if they are not visible */
	public static final int TURNS_BEFORE_END_COMBAT = 5;
	public static final float PURSUE_TARGET_UTILITY = 0.7f;
	
	private Actor targetActor;
	private int turnsUntilRemove;
	private PursueTarget pursueTarget;
	
	public CombatTarget(Actor actor) {
		this.targetActor = actor;
		this.turnsUntilRemove = TURNS_BEFORE_END_COMBAT;
	}
	
	public void update(Actor subject) {
		if(pursueTarget == null) {
			pursueTarget = new PursueTarget(targetActor.getArea(), PURSUE_TARGET_UTILITY);
			subject.addPursueTarget(pursueTarget);
		}
		if(subject.canSee(targetActor)) {
			turnsUntilRemove = TURNS_BEFORE_END_COMBAT;
			pursueTarget.setTargetArea(targetActor.getArea());
		} else {
			turnsUntilRemove--;
		}
		if(targetActor.isDead()) {
			pursueTarget.markForRemoval();
		}
	}
	
	public boolean shouldRemove() {
		return targetActor.isDead() || turnsUntilRemove <= 0;
	}
	
	public Actor getTargetActor() {
		return targetActor;
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
