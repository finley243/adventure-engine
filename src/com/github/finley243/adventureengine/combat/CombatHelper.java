package com.github.finley243.adventureengine.combat;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.MathUtils;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Limb;
import com.github.finley243.adventureengine.item.ItemWeapon;
import com.github.finley243.adventureengine.world.AttackTarget;

public class CombatHelper {

	// Range of final hit chance (clamped)
	public static final float HIT_CHANCE_MAX = 0.99f;
	public static final float HIT_CHANCE_MIN = 0.01f;
	
	public static float calculateHitChance(Actor attacker, ItemWeapon weapon, AttackTarget target, Limb limb, String attackSkill, String dodgeSkill, float hitChanceBaseMin, float hitChanceBaseMax, boolean canBeDodged, float hitChanceMult) {
		Context context = new Context(attacker.game(), attacker, target, weapon);
		float chance = MathUtils.chanceLogSkill(attacker, attackSkill, hitChanceBaseMin, hitChanceBaseMax, context);
		if (canBeDodged && target instanceof Actor actor && actor.canDodge(context)) {
			int attackerSkill = attacker.getSkill(attackSkill, context);
			int targetSkill = ((Actor) target).getSkill(dodgeSkill, context);
			if (targetSkill >= attackerSkill) {
				int penaltyMult = targetSkill - attackerSkill + 1;
				chance -= penaltyMult * 0.05f;
			}
		}
		chance = weapon.getModifiedHitChance(context, chance);
		if (limb != null) {
			chance *= limb.getHitChance();
		}
		chance *= (hitChanceMult + 1.0f);
		return MathUtils.bound(chance, HIT_CHANCE_MIN, HIT_CHANCE_MAX);
	}

	public static float calculateHitChanceNoTarget(Actor attacker, ItemWeapon weapon, Limb limb, String attackSkill, float hitChanceBaseMin, float hitChanceBaseMax, float hitChanceMult) {
		Context context = new Context(attacker.game(), attacker, attacker, weapon);
		float chance = MathUtils.chanceLogSkill(attacker, attackSkill, hitChanceBaseMin, hitChanceBaseMax, context);
		chance = weapon.getModifiedHitChance(context, chance);
		if (limb != null) {
			chance *= limb.getHitChance();
		}
		chance *= (hitChanceMult + 1.0f);
		return MathUtils.bound(chance, HIT_CHANCE_MIN, HIT_CHANCE_MAX);
	}

	public static float calculateHitChanceDodgeOnly(Actor attacker, AttackTarget target, String attackSkill, String dodgeSkill) {
		if (target instanceof Actor) {
			Context context = new Context(attacker.game(), attacker, target);
			int attackerSkill = attacker.getSkill(attackSkill, context);
			int targetSkill = ((Actor) target).getSkill(dodgeSkill, context);
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
