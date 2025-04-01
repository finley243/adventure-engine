package com.github.finley243.adventureengine.combat;

import com.github.finley243.adventureengine.actor.Limb;

import java.util.List;

public class Damage {

    private final String type;
    private final int amount;
    private final Limb limb;
    // Multiplier for armor value of target (1.0f = unchanged, 0.0f = ignores armor)
    private final float armorMult;
    private final List<String> targetEffects;

    public Damage(String type, int amount, Limb limb, float armorMult, List<String> targetEffects) {
        if (amount < 0) throw new IllegalArgumentException("Damage amount cannot be less than 0");
        this.type = type;
        this.amount = amount;
        this.limb = limb;
        this.armorMult = armorMult;
        this.targetEffects = targetEffects;
    }

    public String getType() {
        return type;
    }

    public int getAmount() {
        return amount;
    }

    public Limb getLimb() {
        return limb;
    }

    public float getArmorMult() {
        return armorMult;
    }

    public List<String> getTargetEffects() {
        return targetEffects;
    }

}
