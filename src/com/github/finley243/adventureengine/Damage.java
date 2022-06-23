package com.github.finley243.adventureengine;

public class Damage {

    public enum DamageType {
        PHYSICAL, THERMAL, CHEMICAL, EXPLOSIVE
    }

    private final DamageType type;
    private final int amount;
    // Multiplier for armor value of target (1.0f = unchanged, 0.0f = ignores armor)
    private final float armorMult;

    public Damage(DamageType type, int amount, float armorMult) {
        if (amount < 0) throw new IllegalArgumentException("Damage amount cannot be less than 0");
        this.type = type;
        this.amount = amount;
        this.armorMult = armorMult;
    }

    public DamageType getType() {
        return type;
    }

    public int getAmount() {
        return amount;
    }

    public float getArmorMult() {
        return armorMult;
    }

}
