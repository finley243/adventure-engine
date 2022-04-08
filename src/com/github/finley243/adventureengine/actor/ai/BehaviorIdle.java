package com.github.finley243.adventureengine.actor.ai;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.AudioVisualEvent;
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
	
	private AreaTarget currentTarget;
	
	public BehaviorIdle(List<String> steps) {
		this.steps = steps;
		this.stepIndex = 0;
		this.stepTurnCounter = 0;
	}
	
	public void update(Actor subject) {
		if(steps == null || steps.isEmpty()) {
			return;
		}
		if(currentTarget == null) {
			Set<Area> targetSet = new HashSet<>();
			targetSet.add(subject.game().data().getArea(steps.get(0)));
			currentTarget = new AreaTarget(targetSet, IDLE_MOVEMENT_WEIGHT, steps.size() == 1, false, false);
			subject.addPursueTarget(currentTarget);
		}
		if(subject.isInCombat()) {
			currentTarget.setTargetUtility(0.0f);
		} else {
			currentTarget.setTargetUtility(IDLE_MOVEMENT_WEIGHT);
		}
		if(currentTarget.shouldRemove() && !subject.isInCombat()) {
			if(stepTurnCounter <= 0) {
				stepIndex++;
				if(stepIndex >= steps.size()) {
					stepIndex = 0;
				}
				Set<Area> targetSet = new HashSet<>();
				targetSet.add(subject.game().data().getArea(steps.get(stepIndex)));
				currentTarget = new AreaTarget(targetSet, IDLE_MOVEMENT_WEIGHT, false, false, false);
				subject.addPursueTarget(currentTarget);
				stepTurnCounter = TURNS_PER_STEP;
			} else {
				if(stepTurnCounter == 1) {
					Context context = new Context(subject, getNextArea(subject));
					subject.game().eventBus().post(new AudioVisualEvent(subject.getArea(), Phrases.get("patrolTelegraph"), context, null, null));
				}
				stepTurnCounter--;
			}
		}
	}
	
	private Area getNextArea(Actor subject) {
		if(stepIndex + 1 >= steps.size()) {
			return subject.game().data().getArea(steps.get(0));
		} else {
			return subject.game().data().getArea(steps.get(stepIndex + 1));
		}
	}

}
