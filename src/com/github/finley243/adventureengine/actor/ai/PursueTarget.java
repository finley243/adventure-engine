package com.github.finley243.adventureengine.actor.ai;

import java.util.List;
import java.util.Set;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.world.environment.Area;

public class PursueTarget {

	private Set<Area> targetAreas;
	private float targetUtility;
	private List<Area> path;
	private int pathIndex;
	private final boolean manualRemoval;
	private boolean markForRemoval;
	private boolean isActive;
	private boolean shouldFlee;
	private boolean fleeThroughExits;
	
	public PursueTarget(Set<Area> targetAreas, float targetUtility, boolean manualRemoval, boolean shouldFlee, boolean fleeThroughExits) {
		this.targetAreas = targetAreas;
		this.targetUtility = targetUtility;
		this.manualRemoval = manualRemoval;
		this.shouldFlee = shouldFlee;
		this.fleeThroughExits = fleeThroughExits;
		markForRemoval = false;
		isActive = true;
	}
	
	public void update(Actor subject) {
		if(path == null || !targetAreas.contains(path.get(path.size() - 1))) {
			path = Pathfinder.findPath(subject.getArea(), targetAreas);
			pathIndex = 0;
		}
		if(path.get(pathIndex) != subject.getArea()) {
			int currentIndex = getCurrentIndex(subject);
			if(currentIndex == -1) {
				path = Pathfinder.findPath(subject.getArea(), targetAreas);
				pathIndex = 0;
			} else {
				pathIndex = currentIndex;
			}
		}
	}

	public Set<Area> getTargetAreas() {
		return targetAreas;
	}
	
	public void setTargetAreas(Set<Area> areas) {
		targetAreas = areas;
	}
	
	public float getTargetUtility() {
		return targetUtility;
	}
	
	public void setTargetUtility(float utility) {
		targetUtility = utility;
	}
	
	public void markForRemoval() {
		markForRemoval = true;
	}
	
	public boolean shouldRemove() {
		return markForRemoval || (!manualRemoval && path != null && pathIndex == path.size() - 1);
	}

	public boolean isActive() {
		return isActive;
	}

	public void setIsActive(boolean isActive) {
		this.isActive = isActive;
	}

	public boolean shouldFlee() {
		return shouldFlee;
	}
	
	public void setShouldFlee(boolean shouldFlee) {
		this.shouldFlee = shouldFlee;
	}

	public boolean shouldUseExits() {
		return !shouldFlee || fleeThroughExits;
	}

	public void setFleeThroughExits(boolean fleeThroughExits) {
		this.fleeThroughExits = fleeThroughExits;
	}
	
	public boolean isOnPath(Area area) {
		if(path == null) return false;
		if(pathIndex + 1 >= path.size()) return false;
		return path.get(pathIndex + 1) == area;
	}
	
	public int getDistance() {
		if(path == null) {
			
		}
		return path.size() - (pathIndex + 1);
	}
	
	private int getCurrentIndex(Actor subject) {
		for(int i = 0; i < path.size(); i++) {
			if(path.get(i) == subject.getArea()) {
				return i;
			}
		}
		return -1;
	}
	
	@Override
	public boolean equals(Object other) {
		if(!(other instanceof PursueTarget)) {
			return false;
		} else {
			return this == other;
		}
	}
	
	@Override
	public int hashCode() {
		return getTargetAreas().hashCode();
	}

}
