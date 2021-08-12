package com.github.finley243.adventureengine.actor.ai;

import java.util.List;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.world.environment.Area;

public class BehaviorIdle {

	public static final int TURNS_PER_STEP = 1;
	
	// null = wander within room, 1 = stationary, >1 = patrol path (uses shortest path between points)
	private List<Area> steps;
	
	private int stepIndex;
	private int stepTurnCounter;
	
	private List<Area> pathToTarget;
	
	public BehaviorIdle(List<Area> steps) {
		this.steps = steps;
		if(steps != null && !steps.isEmpty()) {
			this.stepIndex = 0;
		}
		this.stepTurnCounter = 0;
	}
	
	public void update(Actor subject) {
		
	}
	
	private boolean isAtTargetArea(Actor subject) {
		return subject.getArea() == steps.get(stepIndex);
	}

}
