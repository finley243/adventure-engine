package com.github.finley243.adventureengine.actor;

import java.util.List;

import com.github.finley243.adventureengine.world.environment.Area;

public class PursueTarget {

	private Area targetArea;
	private float targetUtility;
	private List<Area> path;
	private int pathIndex;
	private boolean markForRemoval;
	
	public PursueTarget(Area targetArea, float targetUtility) {
		this.targetArea = targetArea;
		this.targetUtility = targetUtility;
		markForRemoval = false;
	}
	
	public void update(Actor subject) {
		if(path == null || path.get(path.size() - 1) != targetArea) {
			path = Pathfinder.findPath(subject.getArea(), targetArea);
			pathIndex = 0;
		}
		if(path.get(pathIndex) != subject.getArea()) {
			int currentIndex = getCurrentIndex(subject);
			if(currentIndex == -1) {
				path = Pathfinder.findPath(subject.getArea(), targetArea);
				pathIndex = 0;
			} else {
				pathIndex = currentIndex;
			}
		}
	}
	
	public Area getTargetArea() {
		return targetArea;
	}
	
	public void setTargetArea(Area area) {
		targetArea = area;
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
		return markForRemoval || pathIndex == path.size() - 1;
	}
	
	public boolean isOnPath(Area area) {
		return path.get(pathIndex + 1) == area;
	}
	
	public int getDistance() {
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
			return this.getTargetArea().equals(((PursueTarget) other).getTargetArea());
		}
	}
	
	@Override
	public int hashCode() {
		return getTargetArea().hashCode();
	}

}
