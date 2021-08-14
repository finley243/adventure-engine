package com.github.finley243.adventureengine.actor.ai;

import com.github.finley243.adventureengine.actor.Actor;

public class CombatTarget {

	/** Number of turns it takes for a combat target to be removed if they are not visible */
	public static final int TURNS_BEFORE_END_COMBAT = 5;
	public static final float PURSUE_TARGET_UTILITY_MELEE = 0.7f;
	public static final float PURSUE_TARGET_UTILITY_RANGED = 0.0f;
	public static final float PURSUE_TARGET_UTILITY_INVISIBLE = 0.6f;
	public static final float PURSUE_TARGET_UTILITY_NOWEAPON = 0.0f;
	public static final float FLEE_TARGET_UTILITY = 0.5f;
	
	private Actor targetActor;
	private int turnsUntilRemove;
	private PursueTarget pursueTarget;
	
	public CombatTarget(Actor actor) {
		System.out.println("CombatTarget constructed - target: " + actor.getName());
		this.targetActor = actor;
		this.turnsUntilRemove = TURNS_BEFORE_END_COMBAT;
	}
	
	public void update(Actor subject) {
		if(pursueTarget == null) {
			pursueTarget = new PursueTarget(targetActor.getArea(), PURSUE_TARGET_UTILITY_RANGED, true, false);
			subject.addPursueTarget(pursueTarget);
		}
		if(subject.canSee(targetActor)) {
			turnsUntilRemove = TURNS_BEFORE_END_COMBAT;
			pursueTarget.setTargetArea(targetActor.getArea());
			pursueTarget.setShouldFlee(subject.shouldFleeFrom(targetActor));
			if(subject.shouldFleeFrom(targetActor)) {
				pursueTarget.setTargetUtility(FLEE_TARGET_UTILITY);
			} else if(subject.hasRangedWeaponEquipped()) {
				pursueTarget.setTargetUtility(PURSUE_TARGET_UTILITY_RANGED);
			} else if(subject.hasMeleeWeaponEquipped()) {
				pursueTarget.setTargetUtility(PURSUE_TARGET_UTILITY_MELEE);
			} else {
				pursueTarget.setTargetUtility(PURSUE_TARGET_UTILITY_NOWEAPON);
			}
		} else {
			pursueTarget.setTargetUtility(PURSUE_TARGET_UTILITY_INVISIBLE);
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
