package com.github.finley243.adventureengine.actor;

import com.github.finley243.adventureengine.actor.ai.Pathfinder;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.world.item.ItemWeapon;

public class CombatHelper {

	public static final float HIT_CHANCE_MAX = 0.99f;
	public static final float HIT_CHANCE_MIN = 0.01f;
	public static final float HIT_CHANCE_BASE_MAX = 0.95f;
	public static final float HIT_CHANCE_BASE_MIN = 0.05f;
	public static final float RANGE_PENALTY = 0.05f;
	public static final float RANGE_PENALTY_MAX = 0.40f;
	public static final float EVASION_PENALTY = 0.05f;

	public static final float AUTOFIRE_DAMAGE_MULT = 4.00f;
	public static final float AUTOFIRE_HIT_CHANCE_MULT = 0.50f;

	public static Context lastAttack;

	public static void newTurn() {
		lastAttack = null;
	}

	public static boolean isRepeat(Context attackContext) {
		boolean isRepeat = lastAttack != null && lastAttack.getSubject() == attackContext.getSubject() && lastAttack.getObject() == attackContext.getObject() && lastAttack.getObject2() == attackContext.getObject2();
		lastAttack = attackContext;
		return isRepeat;
	}
	
	public static float calculateHitChance(Actor attacker, Actor target, Limb limb, ItemWeapon weapon, boolean auto) {
		float skill = (float) attacker.getSkill(weapon.getSkill());
		float chance = HIT_CHANCE_BASE_MIN + ((HIT_CHANCE_BASE_MAX - HIT_CHANCE_BASE_MIN) / (Actor.SKILL_MAX - Actor.SKILL_MIN)) * (skill - Actor.SKILL_MIN);
		if(limb != null) {
			chance *= limb.getHitChance();
		}
		if(auto) {
			chance *= AUTOFIRE_HIT_CHANCE_MULT;
		}
		if(weapon.isRanged()) {
			int distance = Pathfinder.findPath(attacker.getArea(), target.getArea()).size() - 1;
			int distFromRange = Math.abs(weapon.getRange() - distance);
			float rangePenalty = Math.min(distFromRange * RANGE_PENALTY, RANGE_PENALTY_MAX);
			chance -= rangePenalty;
		} else {
			float evasionSkill = (float) target.getSkill(Actor.Skill.EVASION);
			float meleeEvasionMod = EVASION_PENALTY * evasionSkill;
			chance -= meleeEvasionMod;
		}
		if(chance < HIT_CHANCE_MIN) {
			return HIT_CHANCE_MIN;
		} else if (chance > HIT_CHANCE_MAX) {
			return HIT_CHANCE_MAX;
		} else {
			return chance;
		}
	}

	public static String getHitPhrase(ItemWeapon weapon, Limb limb, boolean crit, boolean auto) {
		if(weapon.isRanged()) {
			if(auto) {
				return crit ? "rangedAutoHitCrit" : "rangedAutoHit";
			} else if(limb != null) {
				return crit ? limb.getRangedCritHitPhrase() : limb.getRangedHitPhrase();
			} else {
				return crit ? "rangedHitCrit" : "rangedHit";
			}
		} else {
			if(limb != null) {
				return crit ? limb.getMeleeCritHitPhrase() : limb.getMeleeHitPhrase();
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
				return limb.getRangedMissPhrase();
			} else {
				return "rangedMiss";
			}
		} else {
			if(limb != null) {
				return limb.getMeleeMissPhrase();
			} else {
				return "meleeMiss";
			}
		}
	}

}
