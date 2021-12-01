package com.github.finley243.adventureengine.actor.ai;

import java.util.List;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.world.environment.Area;

public class PursueTarget {

	private Area targetArea;
	private float targetUtility;
	private List<Area> path;
	private int pathIndex;
	private final boolean manualRemoval;
	private boolean markForRemoval;
	private boolean shouldFlee;
	
	public PursueTarget(Area targetArea, float targetUtility, boolean manualRemoval, boolean shouldFlee) {
		this.targetArea = targetArea;
		this.targetUtility = targetUtility;
		this.manualRemoval = manualRemoval;
		this.shouldFlee = shouldFlee;
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
		return markForRemoval || (!manualRemoval && path != null && pathIndex == path.size() - 1);
	}
	
	public boolean shouldFlee() {
		return shouldFlee;
	}
	
	public void setShouldFlee(boolean shouldFlee) {
		this.shouldFlee = shouldFlee;
	}
	
	public boolean isOnPath(Area area) {
		if(path == null) return false;
		if(pathIndex + 1 >= path.size()) return false;
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
			return this == other;
		}
	}
	
	@Override
	public int hashCode() {
		return getTargetArea().hashCode();
	}

}
