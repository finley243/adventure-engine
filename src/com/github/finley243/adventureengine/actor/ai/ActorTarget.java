package com.github.finley243.adventureengine.actor.ai;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.item.ItemWeapon;

import java.util.HashSet;
import java.util.Set;

public class ActorTarget {

	/** Number of turns it takes for a target to be removed if they are not visible */
	public static final int TURNS_BEFORE_END_COMBAT = 5;
	
	private final Actor targetActor;
	private final boolean isEnemy;

	private boolean markForRemoval;
	private int turnsUntilRemove;
	private AreaTarget areaTarget;
	private Area lastKnownArea;
	
	public ActorTarget(Actor actor, boolean isEnemy) {
		this.targetActor = actor;
		this.isEnemy = isEnemy;
		this.turnsUntilRemove = TURNS_BEFORE_END_COMBAT;
		areaTarget = null;
		lastKnownArea = actor.getArea();
	}

	public void markForRemoval() {
		markForRemoval = true;
	}

	public boolean isEnemy() {
		return isEnemy;
	}

	public void nextTurn() {
		if(turnsUntilRemove > 0) {
			turnsUntilRemove--;
		}
	}
	
	public void update(Actor subject) {
		if(areaTarget == null) {
			if(isEnemy) {
				areaTarget = new AreaTarget(idealAreas(subject, lastKnownArea), 0.0f, true, false, false);
			} else {
				areaTarget = new AreaTarget(Set.of(lastKnownArea), 0.0f, true, false, false);
			}
			subject.addPursueTarget(areaTarget);
		}
		if(subject.canSee(targetActor)) {
			lastKnownArea = targetActor.getArea();
			turnsUntilRemove = TURNS_BEFORE_END_COMBAT;
			areaTarget.setTargetAreas(idealAreas(subject, lastKnownArea));
			areaTarget.setShouldFlee(UtilityUtils.shouldMoveAwayFrom(subject, this));
			areaTarget.setIsActive(UtilityUtils.shouldActivatePursueTarget(subject, this));
			areaTarget.setTargetUtility(UtilityUtils.getPursueTargetUtility(subject, targetActor));
		} else {
			areaTarget.setTargetAreas(idealAreas(subject, lastKnownArea));
			areaTarget.setTargetUtility(UtilityUtils.getPursueInvisibleTargetUtility());
		}
		if(shouldRemove()) {
			areaTarget.markForRemoval();
		}
	}

	public void setLastKnownArea(Area area) {
		lastKnownArea = area;
	}
	
	public boolean shouldRemove() {
		return targetActor.isDead() || (isEnemy && turnsUntilRemove <= 0) || markForRemoval;
	}
	
	public Actor getTargetActor() {
		return targetActor;
	}
	
	@Override
	public boolean equals(Object other) {
		if(!(other instanceof ActorTarget)) {
			return false;
		} else {
			return this.getTargetActor().equals(((ActorTarget) other).getTargetActor());
		}
	}
	
	@Override
	public int hashCode() {
		return getTargetActor().hashCode();
	}

	private Set<Area> idealAreas(Actor subject, Area origin) {
		int idealDistanceMin = 0;
		int idealDistanceMax = 0;
		if(subject.hasRangedWeaponEquipped()) {
			ItemWeapon weapon = (ItemWeapon) subject.getEquippedItem();
			idealDistanceMin = weapon.getRangeMin();
			idealDistanceMax = weapon.getRangeMax();
		}
		if(idealDistanceMax == 0) {
			Set<Area> idealAreas = new HashSet<>();
			idealAreas.add(origin);
			return idealAreas;
		}
		return origin.visibleAreasInRange(idealDistanceMin, idealDistanceMax);
	}
	
}