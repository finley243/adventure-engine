package com.github.finley243.adventureengine.combat;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.MathUtils;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Limb;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.item.component.ItemComponentWeapon;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.world.AttackTarget;

public class CombatHelper {

	// Range of final hit chance (clamped)
	public static final float HIT_CHANCE_MAX = 0.99f;
	public static final float HIT_CHANCE_MIN = 0.01f;
	
	public static float calculateHitChance(Context scriptContext, Item weapon, Limb limb, Script hitChanceFunction, float hitChanceMult) {
		Script.ScriptReturnData hitChanceResult = hitChanceFunction.execute(, scriptContext);
		if (hitChanceResult.error() != null) {
			throw new RuntimeException("Error calculating hit chance: " + hitChanceResult.stackTrace());
		} else if (hitChanceResult.flowStatement() != null) {
			throw new RuntimeException("Unexpected flow statement in hit chance expression");
		} else if (hitChanceResult.value() == null) {
			throw new RuntimeException("Hit chance expression returned null");
		} else if (hitChanceResult.value().getDataType() != Expression.DataType.FLOAT) {
			throw new RuntimeException("Hit chance expression did not return a float");
		}
		float chance = hitChanceResult.value().getValueFloat();
		if (weapon != null) {
			// TODO - Find a way to allow hit chance effects on unarmed attacks (no weapon)
			chance = weapon.getComponentOfType(ItemComponentWeapon.class).getModifiedHitChance(scriptContext, chance);
		}
		if (limb != null) {
			chance *= limb.getHitChance();
		}
		chance *= (hitChanceMult + 1.0f);
		return MathUtils.bound(chance, HIT_CHANCE_MIN, HIT_CHANCE_MAX);
	}

	public static float calculateHitChanceNoTarget(Context scriptContext, Item weapon, Limb limb, Script hitChanceFunction, float hitChanceMult) {
		Script.ScriptReturnData hitChanceResult = hitChanceFunction.execute(, scriptContext);
		if (hitChanceResult.error() != null) {
			throw new RuntimeException("Error calculating hit chance: " + hitChanceResult.stackTrace());
		} else if (hitChanceResult.flowStatement() != null) {
			throw new RuntimeException("Unexpected flow statement in hit chance expression");
		} else if (hitChanceResult.value() == null) {
			throw new RuntimeException("Hit chance expression returned null");
		} else if (hitChanceResult.value().getDataType() != Expression.DataType.FLOAT) {
			throw new RuntimeException("Hit chance expression did not return a float");
		}
		float chance = hitChanceResult.value().getValueFloat();
		if (weapon != null) {
			// TODO - Find a way to allow hit chance effects on unarmed attacks (no weapon)
			chance = weapon.getComponentOfType(ItemComponentWeapon.class).getModifiedHitChance(scriptContext, chance);
		}
		if (limb != null) {
			chance *= limb.getHitChance();
		}
		chance *= (hitChanceMult + 1.0f);
		return MathUtils.bound(chance, HIT_CHANCE_MIN, HIT_CHANCE_MAX);
	}

	public static float calculateHitChanceDodgeOnly(Context scriptContext, AttackTarget target, Script hitChanceFunction) {
		if (target instanceof Actor) {
			Script.ScriptReturnData hitChanceResult = hitChanceFunction.execute(, scriptContext);
			if (hitChanceResult.error() != null) {
				throw new RuntimeException("Error calculating hit chance: " + hitChanceResult.stackTrace());
			} else if (hitChanceResult.flowStatement() != null) {
				throw new RuntimeException("Unexpected flow statement in hit chance expression");
			} else if (hitChanceResult.value() == null) {
				throw new RuntimeException("Hit chance expression returned null");
			} else if (hitChanceResult.value().getDataType() != Expression.DataType.FLOAT) {
				throw new RuntimeException("Hit chance expression did not return a float");
			}
            return hitChanceResult.value().getValueFloat();
		} else {
			return 1.0f;
		}
	}

}
