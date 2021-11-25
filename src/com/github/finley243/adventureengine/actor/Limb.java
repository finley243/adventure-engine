package com.github.finley243.adventureengine.actor;

import com.github.finley243.adventureengine.effect.Effect;

import java.util.List;

public class Limb {

    private final String name;
    private final float hitChance;
    private final float damageMult;
    private final ApparelManager.ApparelSlot apparelSlot;
    private final int maxCondition;
    private final List<Effect> crippledEffects;

    public Limb(String name, float hitChance, float damageMult, ApparelManager.ApparelSlot apparelSlot, int maxCondition, List<Effect> crippledEffects) {
        this.name = name;
        this.hitChance = hitChance;
        this.damageMult = damageMult;
        this.apparelSlot = apparelSlot;
        this.maxCondition = maxCondition;
        this.crippledEffects = crippledEffects;
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

    public ApparelManager.ApparelSlot getApparelSlot() {
        return apparelSlot;
    }

    public int getMaxCondition() {
        return maxCondition;
    }

    public void setCrippled(boolean isCrippled, Actor target) {
        for(Effect effect : crippledEffects) {
            if(isCrippled) {
                target.addEffect(effect);
            } else {
                target.removeEffect(effect);
            }
        }
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

}
