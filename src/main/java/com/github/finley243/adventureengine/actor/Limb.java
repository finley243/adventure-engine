package com.github.finley243.adventureengine.actor;

import java.util.List;

public class Limb {

    private final String ID;
    private final String name;
    private final float hitChance;
    private final float damageMult;
    private final String apparelSlot;
    private final List<String> hitEffects;

    public Limb(String ID, String name, float hitChance, float damageMult, String apparelSlot, List<String> hitEffects) {
        this.ID = ID;
        this.name = name;
        this.hitChance = hitChance;
        this.damageMult = damageMult;
        this.apparelSlot = apparelSlot;
        this.hitEffects = hitEffects;
    }

    public String getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public float getHitChance() {
        return hitChance;
    }

    public float getDamageMult() {
        return damageMult;
    }

    public String getApparelSlot() {
        return apparelSlot;
    }

    public void applyEffects(Actor target) {
        for(String effectID : hitEffects) {
            target.getEffectComponent().addEffect(effectID);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Limb otherLimb) {
            return otherLimb.getID().equals(this.getID());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return getID().hashCode();
    }

}
