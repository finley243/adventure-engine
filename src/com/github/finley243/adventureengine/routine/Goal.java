package com.github.finley243.adventureengine.routine;

public class Goal {

	public enum GoalType {
		MOVE, ATTACK
	}
	
	private GoalType type;
	private String targetID;
	
	public Goal() {
		
	}
	
	public GoalType getType() {
		return type;
	}
	
	public String getTargetID() {
		return targetID;
	}
	
}
