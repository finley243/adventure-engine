package com.github.finley243.adventureengine.actor.ai;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.object.ObjectElevator;
import com.github.finley243.adventureengine.world.object.ObjectExit;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class CombatTarget {

	/** Number of turns it takes for a combat target to be removed if they are not visible */
	public static final int TURNS_BEFORE_END_COMBAT = 5;
	
	private final Actor targetActor;
	private int turnsUntilRemove;
	private PursueTarget pursueTarget;
	private ObjectExit usedExit;
	private ObjectElevator usedElevator;
	
	public CombatTarget(Actor actor) {
		this.targetActor = actor;
		this.turnsUntilRemove = TURNS_BEFORE_END_COMBAT;
		pursueTarget = null;
		usedExit = null;
		usedElevator = null;
	}
	
	public void update(Actor subject) {
		if(pursueTarget == null) {
			pursueTarget = new PursueTarget(targetActor.getArea(), 0.0f, true, false);
			subject.addPursueTarget(pursueTarget);
		}
		if(subject.canSee(targetActor)) {
			usedExit = null;
			usedElevator = null;
			turnsUntilRemove = TURNS_BEFORE_END_COMBAT;
			pursueTarget.setTargetArea(targetActor.getArea());
			pursueTarget.setShouldFlee(subject.shouldFleeFrom(targetActor));
			pursueTarget.setTargetUtility(UtilityUtils.getPursueTargetUtility(subject, targetActor));
		} else {
			if(usedExit != null) {
				pursueTarget.setTargetArea(usedExit.getLinkedArea());
				usedExit = null;
			} else if(usedElevator != null) {
				List<Area> linkedAreas = new ArrayList<>(usedElevator.getLinkedAreas());
				pursueTarget.setTargetArea(linkedAreas.get(ThreadLocalRandom.current().nextInt(linkedAreas.size())));
				usedElevator = null;
			}
			pursueTarget.setTargetUtility(UtilityUtils.getPursueInvisibleTargetUtility());
			turnsUntilRemove--;
		}
		if(shouldRemove()) {
			pursueTarget.markForRemoval();
		}
	}

	public void setUsedExit(ObjectExit exit) {
		this.usedExit = exit;
		usedElevator = null;
	}

	public void setUsedElevator(ObjectElevator elevator) {
		this.usedElevator = elevator;
		usedExit = null;
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
