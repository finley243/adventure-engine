package com.github.finley243.adventureengine.actor.ai;

import com.github.finley243.adventureengine.MathUtils;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.component.TargetingComponent;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.ItemWeapon;
import com.github.finley243.adventureengine.world.environment.Area;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class UtilityUtils {

	public static final float PURSUE_TARGET_UTILITY_MELEE = 0.7f;
	public static final float PURSUE_TARGET_UTILITY_RANGED = 0.6f;
	public static final float PURSUE_TARGET_UTILITY_INVISIBLE = 0.7f;
	public static final float PURSUE_TARGET_UTILITY_UNARMED = 0.0f;

	public static final float MOVE_UTILITY_MULTIPLIER = 0.7f;

	public static float getMovementUtility(Actor subject, Area area) {
		float utility = 0.0f;
		for (AreaTarget target : subject.getPursueTargets()) {
			if (target.isOnPath(area)) {
				utility += target.getTargetUtility();
			}
		}
		// TODO - Add utility for possible actions in that area (cover, weapons, etc.)
		return MathUtils.bound(utility, 0.0f, 1.0f);
	}

	public static float getCoverUtility(Actor subject) {
		if (subject.getTargetingComponent() == null) return 0.0f;
		int targetsBlocked = 0;
		int totalTargets = 0;
		for (Actor target : subject.getTargetingComponent().getTargetsOfType(TargetingComponent.DetectionState.HOSTILE)) {
			Area lastKnownArea = subject.getTargetingComponent().getLastKnownArea(target);
			if (target.getEquipmentComponent().hasRangedWeaponEquipped() && lastKnownArea != null && lastKnownArea != subject.getArea()) {
				targetsBlocked++;
			}
			totalTargets++;
		}
		return 0.2f * (((float) targetsBlocked) / ((float) totalTargets));
	}
	
	public static float getPursueTargetUtility(Actor subject, Actor target) {
		if (subject.canSee(target)) {
			return PURSUE_TARGET_UTILITY_INVISIBLE;
		} else if (subject.getEquipmentComponent().hasRangedWeaponEquipped()) {
			return PURSUE_TARGET_UTILITY_RANGED;
		} else if (subject.getEquipmentComponent().hasMeleeWeaponEquipped()) {
			return PURSUE_TARGET_UTILITY_MELEE;
		} else {
			return PURSUE_TARGET_UTILITY_UNARMED;
		}
	}

	public static Action selectActionByUtility(Actor actor, List<Action> actions, int chaos) {
		List<List<Action>> bestActions = new ArrayList<>(chaos + 1);
		List<Float> maxWeights = new ArrayList<>(chaos + 1);
		for (int i = 0; i < chaos + 1; i++) {
			bestActions.add(new ArrayList<>());
			maxWeights.add(0.0f);
		}
		for (Action currentAction : actions) {
			if (currentAction.canChoose(actor)) {
				float currentWeight = currentAction.utility(actor);
				float behaviorOverride = actor.getBehaviorComponent().actionUtilityOverride(currentAction);
				if (behaviorOverride >= 0.0f) {
					currentWeight = behaviorOverride;
				}
				if (currentWeight != 0) {
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
		for (float weight : maxWeights) {
			weightSum += weight;
		}
		float partialWeightSum = 0.0f;
		float random = ThreadLocalRandom.current().nextFloat();
		for (int i = 0; i < chaos + 1; i++) {
			if (random < partialWeightSum + (maxWeights.get(i) / weightSum)) {
				return bestActions.get(i).get(ThreadLocalRandom.current().nextInt(bestActions.get(i).size()));
			} else {
				partialWeightSum += (maxWeights.get(i) / weightSum);
			}
		}
		return null;
	}

	public static boolean actorHasWeapon(Actor actor) {
		for (Item item : actor.getInventory().getItems()) {
			if (item instanceof ItemWeapon) {
				return true;
			}
		}
		return false;
	}

	public static boolean actorHasMeleeTargets(Actor actor) {
		return actor.getTargetingComponent().hasTargetsOfTypeInArea(TargetingComponent.DetectionState.HOSTILE, actor.getArea());
	}

}
