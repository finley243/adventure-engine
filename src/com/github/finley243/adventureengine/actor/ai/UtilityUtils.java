package com.github.finley243.adventureengine.actor.ai;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.item.ItemWeapon;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class UtilityUtils {

	public static final float PURSUE_TARGET_UTILITY_MELEE = 0.7f;
	public static final float PURSUE_TARGET_UTILITY_RANGED = 0.0f;
	public static final float PURSUE_TARGET_UTILITY_INVISIBLE = 0.6f;
	public static final float PURSUE_TARGET_UTILITY_NOWEAPON = 0.0f;
	public static final float FLEE_TARGET_UTILITY = 0.5f;
	public static final float INVESTIGATE_NOISE_UTILITY = 0.5f;
	
	public static float getMovementUtility(Actor subject, Area area, boolean throughDoors) {
		if(subject.getPursueTargets().isEmpty()) {
			return 0.0f;
		}
		float utility = 0.0f;
		int contributors = 0;
		for(AreaTarget target : subject.getPursueTargets()) {
			if(target.isActive() && (!throughDoors || target.shouldUseDoors())) {
				if (target.shouldFlee() && target.getTargetAreas().contains(subject.getArea())) {
					utility += target.getTargetUtility();
					contributors++;
				} else if (target.shouldFlee() ^ target.isOnPath(area)) { // XOR
					// Temporary calculation, ignores distance
					utility += target.getTargetUtility();
					contributors++;
				}
			}
		}
		return utility / contributors;
	}

	public static float getCoverUtility(Actor subject) {
		if(subject.targetingComponent() == null) return 0.0f;
		int targetsBlocked = 0;
		int totalTargets = 0;
		for(Actor target : subject.targetingComponent().getCombatants()) {
			Area lastKnownArea = subject.targetingComponent().getLastKnownArea(target);
			if (target.equipmentComponent().hasRangedWeaponEquipped() && lastKnownArea != null && lastKnownArea != subject.getArea()) {
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

	public static Action selectActionByUtility(Actor actor, List<Action> actions, int chaos) {
		List<List<Action>> bestActions = new ArrayList<>(chaos + 1);
		List<Float> maxWeights = new ArrayList<>(chaos + 1);
		for(int i = 0; i < chaos + 1; i++) {
			bestActions.add(new ArrayList<>());
			maxWeights.add(0.0f);
		}
		for(Action currentAction : actions) {
			if(currentAction.canChoose(actor)) {
				float currentWeight = currentAction.utility(actor);
				float behaviorOverride = actor.behaviorComponent().actionUtilityOverride(currentAction);
				if(behaviorOverride >= 0.0f) {
					currentWeight = behaviorOverride;
				}
				if(currentWeight != 0) {
					for (int i = 0; i < chaos + 1; i++) {
						if (currentWeight == maxWeights.get(i)) {
							bestActions.get(i).add(currentAction);
							break;
						} else if (currentWeight > maxWeights.get(i)) {
							maxWeights.remove(maxWeights.size() - 1);
							maxWeights.add(i, currentWeight);
							bestActions.remove(bestActions.size() - 1);
							bestActions.add(i, new ArrayList<>());
							bestActions.get(i).add(currentAction);
							break;
						}
					}
				}
			}
		}
		float weightSum = 0.0f;
		for(float weight : maxWeights) {
			weightSum += weight;
		}
		float partialWeightSum = 0.0f;
		float random = ThreadLocalRandom.current().nextFloat();
		for(int i = 0; i < chaos + 1; i++) {
			if(random < partialWeightSum + (maxWeights.get(i) / weightSum)) {
				return bestActions.get(i).get(ThreadLocalRandom.current().nextInt(bestActions.get(i).size()));
			} else {
				partialWeightSum += (maxWeights.get(i) / weightSum);
			}
		}
		return null;
	}

}
