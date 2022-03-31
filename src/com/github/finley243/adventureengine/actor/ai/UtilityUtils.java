package com.github.finley243.adventureengine.actor.ai;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.item.ItemWeapon;

public class UtilityUtils {

	public static final float PURSUE_TARGET_UTILITY_MELEE = 0.7f;
	public static final float PURSUE_TARGET_UTILITY_RANGED = 0.0f;
	public static final float PURSUE_TARGET_UTILITY_INVISIBLE = 0.6f;
	public static final float PURSUE_TARGET_UTILITY_NOWEAPON = 0.0f;
	public static final float FLEE_TARGET_UTILITY = 0.5f;
	public static final float INVESTIGATE_NOISE_UTILITY = 0.5f;
	
	public static float getMovementUtility(Actor subject, Area area, boolean throughExit) {
		if(subject.getPursueTargets().isEmpty()) {
			return 0.0f;
		}
		float utility = 0.0f;
		int contributers = 0;
		for(AreaTarget target : subject.getPursueTargets()) {
			if(target.isActive() && (!throughExit || target.shouldUseExits())) {
				if (target.shouldFlee() && target.getTargetAreas().contains(subject.getArea())) {
					utility += target.getTargetUtility();
					contributers++;
				} else if (target.shouldFlee() ^ target.isOnPath(area)) { // XOR
					// Temporary calculation, ignores distance
					utility += target.getTargetUtility();
					contributers++;
				}
			}
		}
		return utility / contributers;
	}

	public static float getCoverUtility(Actor subject) {
		int targetsBlocked = 0;
		int totalTargets = 0;
		for(Actor target : subject.targetingComponent().getCombatants()) {
			Area lastKnownArea = subject.targetingComponent().getLastKnownArea(target);
			if (target.equipmentComponent().hasRangedWeaponEquipped() && lastKnownArea != null && lastKnownArea.isBehindCover(subject.getArea())) {
				targetsBlocked++;
			}
			totalTargets++;
		}
		return 0.2f * (((float) targetsBlocked) / ((float) totalTargets));
	}
	
	public static float getPursueTargetUtility(Actor subject, Actor target) {
		if(shouldFleeFrom(subject, target)) {
			return FLEE_TARGET_UTILITY;
		} else if(subject.equipmentComponent().hasRangedWeaponEquipped()) {
			return PURSUE_TARGET_UTILITY_RANGED;
		} else if(subject.equipmentComponent().hasMeleeWeaponEquipped()) {
			return PURSUE_TARGET_UTILITY_MELEE;
		} else {
			return PURSUE_TARGET_UTILITY_NOWEAPON;
		}
	}

	public static boolean shouldFleeFrom(Actor subject, Actor target) {
		return !subject.equipmentComponent().hasMeleeWeaponEquipped() && target.equipmentComponent().hasMeleeWeaponEquipped();
	}

	// Returns true if subject needs to move to get into ideal range for attacking target (false if already in ideal range)
	public static boolean shouldActivatePursueTarget(Actor subject, Actor target) {
		if(subject.equipmentComponent().hasEquippedItem() && subject.equipmentComponent().getEquippedItem() instanceof ItemWeapon) {
			ItemWeapon weapon = (ItemWeapon) subject.equipmentComponent().getEquippedItem();
			//int targetDistance = target.getTargetDistance();
			int targetDistance = subject.getArea().getDistanceTo(target.getArea().getID());
			return targetDistance == -1 || targetDistance < weapon.getRangeMin() || targetDistance > weapon.getRangeMax();
		} else {
			return true;
		}
	}

	public static boolean shouldMoveAwayFrom(Actor subject, Actor target) {
		if(subject.equipmentComponent().hasRangedWeaponEquipped()) {
			int rangeMin = ((ItemWeapon) subject.equipmentComponent().getEquippedItem()).getRangeMin();
			//return target.getTargetDistance() < rangeMin;
			int distance = subject.getArea().getDistanceTo(target.getArea().getID());
			if(distance == -1) {
				return false;
			}
			return distance < rangeMin;
		} else {
			return !subject.equipmentComponent().hasMeleeWeaponEquipped();
		}
	}
	
	public static float getPursueInvisibleTargetUtility() {
		return PURSUE_TARGET_UTILITY_INVISIBLE;
	}

}
