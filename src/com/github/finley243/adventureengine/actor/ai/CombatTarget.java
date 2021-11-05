package com.github.finley243.adventureengine.actor.ai;

import com.github.finley243.adventureengine.actor.Actor;

public class CombatTarget {

	/** Number of turns it takes for a combat target to be removed if they are not visible */
	public static final int TURNS_BEFORE_END_COMBAT = 8;
	
	private Actor targetActor;
	private int turnsUntilRemove;
	private PursueTarget pursueTarget;
	
	public CombatTarget(Actor actor) {
		this.targetActor = actor;
		this.turnsUntilRemove = TURNS_BEFORE_END_COMBAT;
		pursueTarget = null;
	}
	
	public void update(Actor subject) {
		if(pursueTarget == null) {
			pursueTarget = new PursueTarget(targetActor.getArea(), 0.0f, true, false);
			subject.addPursueTarget(pursueTarget);
		}
		if(subject.canSee(targetActor)) {
			turnsUntilRemove = TURNS_BEFORE_END_COMBAT;
			pursueTarget.setTargetArea(targetActor.getArea());
			pursueTarget.setShouldFlee(subject.shouldFleeFrom(targetActor));
			pursueTarget.setTargetUtility(UtilityUtils.getPursueTargetUtility(subject, targetActor));
		} else {
			pursueTarget.setTargetUtility(UtilityUtils.getPursueInvisibleTargetUtility());
			turnsUntilRemove--;
		}
		if(shouldRemove()) {
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
