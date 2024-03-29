package com.github.finley243.adventureengine.actor.ai;

import java.util.List;
import java.util.Set;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.world.environment.Area;

public class AreaTarget {

	private Set<Area> targetAreas;
	private float targetUtility;
	private List<Area> path;
	private int pathIndex;
	private final boolean manualRemoval;
	private boolean markForRemoval;

	public AreaTarget(Set<Area> targetAreas, float targetUtility, boolean manualRemoval) {
		this.targetAreas = targetAreas;
		this.targetUtility = targetUtility;
		this.manualRemoval = manualRemoval;
		markForRemoval = false;
	}

	public AreaTarget(Area targetArea, float targetUtility, boolean manualRemoval) {
		this(Set.of(targetArea), targetUtility, manualRemoval);
	}
	
	public void update(Actor subject) {
		if (path == null || !targetAreas.contains(path.get(path.size() - 1))) {
			path = Pathfinder.findPath(subject.getArea(), targetAreas, null);
			pathIndex = 0;
		}
		if (path != null && path.get(pathIndex) != subject.getArea()) {
			int currentIndex = getCurrentIndex(subject);
			if (currentIndex == -1) {
				path = Pathfinder.findPath(subject.getArea(), targetAreas, null);
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

	public void setTargetArea(Area area) {
		targetAreas = Set.of(area);
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

	public boolean isOnPath(Area area) {
		if (path == null) return false;
		if (pathIndex + 1 >= path.size()) return false;
		return path.get(pathIndex + 1) == area;
	}
	
	public int getDistance(Actor subject) {
		if (path == null) {
			path = Pathfinder.findPath(subject.getArea(), targetAreas, null);
			pathIndex = 0;
		}
		return (path == null ? -1 : path.size() - (pathIndex + 1));
	}

	public Area getCurrentTarget() {
		if (path == null) return null;
		return path.get(path.size() - 1);
	}
	
	private int getCurrentIndex(Actor subject) {
		for (int i = 0; i < path.size(); i++) {
			if (path.get(i) == subject.getArea()) {
				return i;
			}
		}
		return -1;
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof AreaTarget)) {
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
