package com.github.finley243.adventureengine.actor;

import com.github.finley243.adventureengine.MathUtils;
import com.github.finley243.adventureengine.item.ItemWeapon;
import com.github.finley243.adventureengine.world.AttackTarget;

public class CombatHelper {

	// Range of final hit chance (clamped)
	public static final float HIT_CHANCE_MAX = 0.99f;
	public static final float HIT_CHANCE_MIN = 0.01f;
	// Range of hit chance based on skill/attribute levels (before any modifiers)
	public static final float HIT_CHANCE_BASE_MAX = 0.90f;
	public static final float HIT_CHANCE_BASE_MIN = 0.20f;
	
	public static float calculateHitChance(Actor attacker, AttackTarget target, Limb limb, Actor.Skill skill, float hitChanceBaseMin, float hitChanceBaseMax, float accuracyBonus, boolean canBeDodged, float hitChanceMult) {
		float chance = MathUtils.chanceLogSkill(attacker, skill, hitChanceBaseMin, hitChanceBaseMax);
		chance += accuracyBonus;
		if (canBeDodged && target instanceof Actor) {
			int weaponSkill = attacker.getSkill(skill);
			int dodgeSkill = ((Actor) target).getSkill(Actor.Skill.DODGE);
			if (dodgeSkill >= weaponSkill) {
				int penaltyMult = dodgeSkill - weaponSkill + 1;
				chance -= penaltyMult * 0.05f;
			}
		}
		if (limb != null) {
			chance *= limb.getHitChance();
		}
		chance *= (hitChanceMult + 1.0f);
		return MathUtils.bound(chance, HIT_CHANCE_MIN, HIT_CHANCE_MAX);
	}

}
