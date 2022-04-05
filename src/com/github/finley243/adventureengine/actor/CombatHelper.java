package com.github.finley243.adventureengine.actor;

import com.github.finley243.adventureengine.actor.ai.Pathfinder;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.world.item.ItemWeapon;

import java.util.Arrays;

public class CombatHelper {

	private static final float HIT_CHANCE_MAX = 0.99f;
	private static final float HIT_CHANCE_MIN = 0.01f;
	private static final float HIT_CHANCE_BASE_MAX = 0.99f;
	private static final float HIT_CHANCE_BASE_MIN = 0.10f;
	private static final float RANGE_PENALTY_FAR = 0.10f;
	private static final float EVASION_PENALTY = 0.02f;
	private static final float HIT_CHANCE_ADD = 0.15f;

	private static final float AUTOFIRE_HIT_CHANCE_MULT = 0.50f;

	public static Context lastAttack;

	public static void newTurn() {
		lastAttack = null;
	}

	public static boolean isRepeat(Context attackContext) {
		boolean isRepeat = lastAttack != null && lastAttack.equals(attackContext);
		lastAttack = attackContext;
		return isRepeat;
	}
	
	public static float calculateHitChance(Actor attacker, Actor target, Limb limb, ItemWeapon weapon, boolean auto) {
		float skill = (float) attacker.getSkill(weapon.getSkill());
		float chance = HIT_CHANCE_BASE_MIN + ((HIT_CHANCE_BASE_MAX - HIT_CHANCE_BASE_MIN) / (Actor.SKILL_MAX - Actor.SKILL_MIN)) * (skill - Actor.SKILL_MIN);
		if(weapon.isRanged()) {
			int distance = attacker.getArea().getDistanceTo(target.getArea().getID());
			if(distance < weapon.getRangeMin()) {
				chance -= RANGE_PENALTY_FAR * (weapon.getRangeMin() - distance);
			} else if(distance > weapon.getRangeMax()) {
				chance -= RANGE_PENALTY_FAR * (distance - weapon.getRangeMax());
			}
		} else {
			float evasionSkill = (float) target.getSkill(Actor.Skill.EVASION);
			float meleeEvasionMod = EVASION_PENALTY * evasionSkill;
			chance -= meleeEvasionMod;
		}
		chance += HIT_CHANCE_ADD;
		if(limb != null) {
			chance *= limb.getHitChance();
		}
		if(auto) {
			chance *= AUTOFIRE_HIT_CHANCE_MULT;
		}
		if(chance < HIT_CHANCE_MIN) {
			return HIT_CHANCE_MIN;
		} else return Math.min(chance, HIT_CHANCE_MAX);
	}

	public static String getHitPhrase(ItemWeapon weapon, Limb limb, boolean crit, boolean auto) {
		if(weapon.isRanged()) {
			if(auto) {
				return crit ? "rangedAutoHitCrit" : "rangedAutoHit";
			} else if(limb != null) {
				return crit ? "rangedHitCritLimb" : "rangedHitLimb";
			} else {
				return crit ? "rangedHitCrit" : "rangedHit";
			}
		} else {
			if(limb != null) {
				return crit ? "meleeHitCritLimb" : "meleeHitLimb";
			} else {
				return crit ? "meleeHitCrit" : "meleeHit";
			}
		}
	}

	public static String getTelegraphPhrase(ItemWeapon weapon, Limb limb, boolean auto) {
		if(weapon.isRanged()) {
			return "rangedTelegraph";
		} else {
			return "meleeTelegraph";
		}
	}

	public static String getMissPhrase(ItemWeapon weapon, Limb limb, boolean auto) {
		if(weapon.isRanged()) {
			if(auto) {
				return "rangedAutoMiss";
			} else if(limb != null) {
				return "rangedMissLimb";
			} else {
				return "rangedMiss";
			}
		} else {
			if(limb != null) {
				return "meleeMissLimb";
			} else {
				return "meleeMiss";
			}
		}
	}

}
