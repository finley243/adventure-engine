package com.github.finley243.adventureengine.combat;

import com.github.finley243.adventureengine.MathUtils;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Limb;
import com.github.finley243.adventureengine.item.ItemWeapon;
import com.github.finley243.adventureengine.world.AttackTarget;

public class CombatHelper {

	// Range of final hit chance (clamped)
	public static final float HIT_CHANCE_MAX = 0.99f;
	public static final float HIT_CHANCE_MIN = 0.01f;
	// Range of hit chance based on skill/attribute levels (before any modifiers)
	public static final float HIT_CHANCE_BASE_MAX = 0.90f;
	public static final float HIT_CHANCE_BASE_MIN = 0.20f;
	
	public static float calculateHitChance(Actor attacker, AttackTarget target, Limb limb, Actor.Skill attackSkill, Actor.Skill dodgeSkill, float hitChanceBaseMin, float hitChanceBaseMax, float accuracyBonus, boolean canBeDodged, float hitChanceMult) {
		float chance = MathUtils.chanceLogSkill(attacker, attackSkill, hitChanceBaseMin, hitChanceBaseMax);
		chance += accuracyBonus;
		if (canBeDodged && target instanceof Actor actor && actor.canDodge()) {
			int attackerSkill = attacker.getSkill(attackSkill);
			int targetSkill = ((Actor) target).getSkill(dodgeSkill);
			if (targetSkill >= attackerSkill) {
				int penaltyMult = targetSkill - attackerSkill + 1;
				chance -= penaltyMult * 0.05f;
			}
		}
		if (limb != null) {
			chance *= limb.getHitChance();
		}
		chance *= (hitChanceMult + 1.0f);
		return MathUtils.bound(chance, HIT_CHANCE_MIN, HIT_CHANCE_MAX);
	}

	public static float calculateHitChanceNoTarget(Actor attacker, Limb limb, Actor.Skill attackSkill, float hitChanceBaseMin, float hitChanceBaseMax, float accuracyBonus, float hitChanceMult) {
		float chance = MathUtils.chanceLogSkill(attacker, attackSkill, hitChanceBaseMin, hitChanceBaseMax);
		chance += accuracyBonus;
		if (limb != null) {
			chance *= limb.getHitChance();
		}
		chance *= (hitChanceMult + 1.0f);
		return MathUtils.bound(chance, HIT_CHANCE_MIN, HIT_CHANCE_MAX);
	}

	public static float calculateHitChanceDodgeOnly(Actor attacker, AttackTarget target, Actor.Skill attackSkill, Actor.Skill dodgeSkill) {
		if (target instanceof Actor) {
			int attackerSkill = attacker.getSkill(attackSkill);
			int targetSkill = ((Actor) target).getSkill(dodgeSkill);
			if (targetSkill >= attackerSkill) {
				int penaltyMult = targetSkill - attackerSkill + 1;
				return 1.0f - (penaltyMult * 0.05f);
			} else {
				return 1.0f;
			}
		} else {
			return 1.0f;
		}
	}

}
