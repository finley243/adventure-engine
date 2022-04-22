package com.github.finley243.adventureengine.actor;

import com.github.finley243.adventureengine.MathUtils;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.world.item.ItemWeapon;

public class CombatHelper {

	public static final float HIT_CHANCE_MAX = 0.99f;
	public static final float HIT_CHANCE_MIN = 0.01f;
	public static final float HIT_CHANCE_BASE_MAX = 0.99f;
	public static final float HIT_CHANCE_BASE_MIN = 0.20f;
	public static final float RANGE_PENALTY_FAR = 0.10f;
	public static final float HIT_CHANCE_ADD = 0.15f;

	public static final float AUTOFIRE_HIT_CHANCE_MULT = -0.50f;

	public static Context lastAttack;

	public static void newTurn() {
		lastAttack = null;
	}

	public static boolean isRepeat(Context attackContext) {
		boolean isRepeat = lastAttack != null && lastAttack.equals(attackContext);
		lastAttack = attackContext;
		return isRepeat;
	}
	
	public static float calculateHitChance(Actor attacker, Actor target, Limb limb, ItemWeapon weapon, float hitChanceMult) {
		int skill = attacker.getSkill(weapon.getSkill());
		float chance = MathUtils.chanceLinear(skill, Actor.SKILL_MIN, Actor.SKILL_MAX, HIT_CHANCE_BASE_MIN, HIT_CHANCE_BASE_MAX);
		if(weapon.isRanged()) {
			int distance = attacker.getArea().getDistanceTo(target.getArea().getID());
			if(distance < weapon.getRangeMin()) {
				chance -= RANGE_PENALTY_FAR * (weapon.getRangeMin() - distance);
			} else if(distance > weapon.getRangeMax()) {
				chance -= RANGE_PENALTY_FAR * (distance - weapon.getRangeMax());
			}
		}
		chance += HIT_CHANCE_ADD;
		chance += weapon.getAccuracyBonus();
		if(limb != null) {
			chance *= limb.getHitChance();
		}
		chance *= (hitChanceMult + 1.0f);
		return Math.max(Math.min(chance, HIT_CHANCE_MAX), HIT_CHANCE_MIN);
	}

}
