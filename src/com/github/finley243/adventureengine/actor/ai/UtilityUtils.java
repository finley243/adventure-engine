package com.github.finley243.adventureengine.actor.ai;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.world.environment.Area;

public class UtilityUtils {

	public static final float PURSUE_TARGET_UTILITY_MELEE = 0.7f;
	public static final float PURSUE_TARGET_UTILITY_RANGED = 0.0f;
	public static final float PURSUE_TARGET_UTILITY_INVISIBLE = 0.6f;
	public static final float PURSUE_TARGET_UTILITY_NOWEAPON = 0.0f;
	public static final float FLEE_TARGET_UTILITY = 0.5f;
	
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
	
	public static float getPursueTargetUtility(Actor subject, Actor target) {
		if(subject.shouldFleeFrom(target)) {
			return FLEE_TARGET_UTILITY;
		} else if(subject.hasRangedWeaponEquipped()) {
			return PURSUE_TARGET_UTILITY_RANGED;
		} else if(subject.hasMeleeWeaponEquipped()) {
			return PURSUE_TARGET_UTILITY_MELEE;
		} else {
			return PURSUE_TARGET_UTILITY_NOWEAPON;
		}
	}
	
	public static float getPursueInvisibleTargetUtility() {
		return PURSUE_TARGET_UTILITY_INVISIBLE;
	}

}
