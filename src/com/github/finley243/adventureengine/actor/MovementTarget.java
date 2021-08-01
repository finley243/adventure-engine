package com.github.finley243.adventureengine.actor;

import java.util.List;

import com.github.finley243.adventureengine.world.environment.Area;

public class MovementTarget {

	private Area targetArea;
	private List<Area> path;
	
	public MovementTarget(Area targetArea) {
		this.targetArea = targetArea;
	}
	
	public boolean isOnPath(Actor subject) {
		return path.contains(subject.getArea());
	}
	
	private void calculatePath(Actor subject) {
		path = Pathfinder.findPath(subject.getArea(), targetArea);
	}

}
