package com.github.finley243.adventureengine.combat;

import com.github.finley243.adventureengine.actor.Limb;
import com.github.finley243.adventureengine.effect.Effect;

import java.util.List;

/**
 * @param armorMult Multiplier for armor value of target (1.0f = unchanged, 0.0f = ignores armor)
 */
public record Damage(DamageType type, int amount, Limb limb, float armorMult, List<Effect> targetEffects) {

    public Damage {
        if (amount < 0) throw new IllegalArgumentException("Damage amount cannot be less than 0");
    }

}
