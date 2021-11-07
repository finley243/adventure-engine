package com.github.finley243.adventureengine.actor.ai;

import java.util.List;

import com.github.finley243.adventureengine.Data;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.VisualEvent;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.environment.Area;

public class BehaviorIdle {

	public static final float IDLE_MOVEMENT_WEIGHT = 0.4f;
	public static final int TURNS_PER_STEP = 1;
	
	// empty = wander within room, 1 = stationary, >1 = patrol path (uses the shortest path between points)
	private final List<String> steps;
	
	private int stepIndex;
	private int stepTurnCounter;
	
	private PursueTarget currentTarget;
	
	public BehaviorIdle(List<String> steps) {
		this.steps = steps;
		this.stepIndex = 0;
		this.stepTurnCounter = 0;
	}
	
	public void update(Actor subject) {
		if(steps.isEmpty()) {
			return;
		}
		if(currentTarget == null) {
			currentTarget = new PursueTarget(Data.getArea(steps.get(0)), IDLE_MOVEMENT_WEIGHT, steps.size() == 1, false);
			subject.addPursueTarget(currentTarget);
		}
		if(currentTarget.shouldRemove()) {
			if(stepTurnCounter <= 0) {
				stepIndex++;
				if(stepIndex >= steps.size()) {
					stepIndex = 0;
				}
				currentTarget = new PursueTarget(Data.getArea(steps.get(stepIndex)), IDLE_MOVEMENT_WEIGHT, false, false);
				subject.addPursueTarget(currentTarget);
				stepTurnCounter = TURNS_PER_STEP;
			} else {
				if(stepTurnCounter == 1) {
					Context context = new Context(subject, false, getNextArea(), false);
					Game.EVENT_BUS.post(new VisualEvent(subject.getArea(), Phrases.get("patrolTelegraph"), context));
				}
				stepTurnCounter--;
			}
		}
	}
	
	private Area getNextArea() {
		if(stepIndex + 1 >= steps.size()) {
			return Data.getArea(steps.get(0));
		} else {
			return Data.getArea(steps.get(stepIndex + 1));
		}
	}

}
