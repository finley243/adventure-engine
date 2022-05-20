package com.github.finley243.adventureengine;

public class Damage {

    public enum DamageType {
        PHYSICAL, THERMAL, CHEMICAL
    }

    private final DamageType type;
    private final int amount;
    private final float armorPierce;

    public Damage(DamageType type, int amount, float armorPierce) {
        this.type = type;
        this.amount = amount;
        this.armorPierce = armorPierce;
    }

    public DamageType getType() {
        return type;
    }

    public int getAmount() {
        return amount;
    }

    public float getArmorPierce() {
        return armorPierce;
    }

}
