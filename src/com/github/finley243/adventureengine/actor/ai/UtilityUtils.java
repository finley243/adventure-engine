package com.github.finley243.adventureengine.actor.ai;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.world.environment.Area;

public class UtilityUtils {

	public static float getMovementUtility(Actor subject, Area area) {
		if(subject.getPursueTargets().isEmpty()) {
			return 0.0f;
		}
		float utility = 0.0f;
		for(PursueTarget target : subject.getPursueTargets()) {
			if(target.shouldFlee() && subject.getArea() == target.getTargetArea()) {
				utility += target.getTargetUtility();
			} else if(target.shouldFlee() ^ target.isOnPath(area)) { // XOR
				// Temporary calculation, ignores distance
				utility += target.getTargetUtility();
			}
		}
		return utility / subject.getPursueTargets().size();
	}

}
