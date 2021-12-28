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
	private Area lastKnownArea;
	
	public CombatTarget(Actor actor) {
		this.targetActor = actor;
		this.turnsUntilRemove = TURNS_BEFORE_END_COMBAT;
		pursueTarget = null;
	}

	public void nextTurn() {
		if(turnsUntilRemove > 0) {
			turnsUntilRemove--;
		}
	}
	
	public void update(Actor subject) {
		if(pursueTarget == null) {
			pursueTarget = new PursueTarget(targetActor.getArea(), 0.0f, true, false, false);
			subject.addPursueTarget(pursueTarget);
		}
		if(subject.canSee(targetActor)) {
			lastKnownArea = targetActor.getArea();
			turnsUntilRemove = TURNS_BEFORE_END_COMBAT;
			pursueTarget.setTargetArea(targetActor.getArea());
			pursueTarget.setShouldFlee(UtilityUtils.shouldMoveAwayFrom(subject, this));
			pursueTarget.setIsActive(UtilityUtils.shouldActivatePursueTarget(subject, this));
			pursueTarget.setTargetUtility(UtilityUtils.getPursueTargetUtility(subject, targetActor));
		} else {
			pursueTarget.setTargetArea(lastKnownArea);
			pursueTarget.setTargetUtility(UtilityUtils.getPursueInvisibleTargetUtility());
		}
		if(shouldRemove()) {
			pursueTarget.markForRemoval();
		}
	}

	public void onMoved(Area area) {
		lastKnownArea = area;
	}

	public void onUsedExit(ObjectExit exit) {
		lastKnownArea = exit.getLinkedArea();
	}

	public void onUsedElevator(ObjectElevator elevator) {
		List<Area> possibleAreas = new ArrayList<>(elevator.getLinkedAreas());
		lastKnownArea = possibleAreas.get(ThreadLocalRandom.current().nextInt(possibleAreas.size()));
	}
	
	public boolean shouldRemove() {
		return targetActor.isDead() || turnsUntilRemove <= 0;
	}
	
	public Actor getTargetActor() {
		return targetActor;
	}

	public int getTargetDistance() {
		return pursueTarget.getDistance();
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
