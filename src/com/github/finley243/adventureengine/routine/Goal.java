package com.github.finley243.adventureengine.routine;

import java.util.List;

import com.github.finley243.adventureengine.world.environment.Area;

public class Goal {

	public enum GoalType {
		MOVE, ATTACK
	}
	
	private GoalType type;
	private String targetID;
	private List<Area> path;
	
	public Goal(GoalType type, String targetID) {
		
	}
	
	public GoalType getType() {
		return type;
	}
	
	public String getTargetID() {
		return targetID;
	}
	
}
