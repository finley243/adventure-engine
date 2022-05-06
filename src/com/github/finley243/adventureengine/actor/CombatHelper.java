package com.github.finley243.adventureengine.actor;

import com.github.finley243.adventureengine.MathUtils;
import com.github.finley243.adventureengine.item.ItemWeapon;

public class CombatHelper {

	// Range of final hit chance (clamped)
	public static final float HIT_CHANCE_MAX = 0.99f;
	public static final float HIT_CHANCE_MIN = 0.01f;
	// Range of hit chance based on skill/attribute levels (before any modifiers)
	public static final float HIT_CHANCE_BASE_MAX = 0.95f;
	public static final float HIT_CHANCE_BASE_MIN = 0.20f;
	// Amount of hit chance to subtract per unit of distance outside of weapon range
	public static final float RANGE_PENALTY = 0.10f;
	
	public static float calculateHitChance(Actor attacker, Actor target, Limb limb, ItemWeapon weapon, float hitChanceMult) {
		float skillToAgilityWeight = (weapon.isRanged() ? 4.0f : 2.0f);
		float chance = MathUtils.chanceLinearSkillAttributeContest(attacker, weapon.getSkill(), target, Actor.Attribute.AGILITY, skillToAgilityWeight, HIT_CHANCE_BASE_MIN, HIT_CHANCE_BASE_MAX);
		if (weapon.isRanged()) {
			int distanceFromRange = MathUtils.differenceFromRange(attacker.getArea().getDistanceTo(target.getArea().getID()), weapon.getRangeMin(), weapon.getRangeMax());
			chance -= RANGE_PENALTY * distanceFromRange;
		}
		chance += weapon.getAccuracyBonus();
		if (limb != null) {
			chance *= limb.getHitChance();
		}
		chance *= (hitChanceMult + 1.0f);
		return MathUtils.bound(chance, HIT_CHANCE_MIN, HIT_CHANCE_MAX);
	}

}
