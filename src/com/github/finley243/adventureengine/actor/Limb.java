package com.github.finley243.adventureengine.actor;

import com.github.finley243.adventureengine.actor.component.ApparelComponent;
import com.github.finley243.adventureengine.effect.Effect;

import java.util.List;

public class Limb {

    private final String name;
    private final float hitChance;
    private final float damageMult;
    private final String apparelSlot;
    private final List<String> hitEffects;

    public Limb(String name, float hitChance, float damageMult, String apparelSlot, List<String> hitEffects) {
        this.name = name;
        this.hitChance = hitChance;
        this.damageMult = damageMult;
        this.apparelSlot = apparelSlot;
        this.hitEffects = hitEffects;
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
            target.effectComponent().addEffect(target.game().data().getEffect(effectID));
        }
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

}
