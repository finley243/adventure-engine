package com.github.finley243.adventureengine.actor;

import com.github.finley243.adventureengine.actor.component.ApparelComponent;
import com.github.finley243.adventureengine.effect.Effect;

import java.util.List;

public class Limb {

    private final String name;
    private final float hitChance;
    private final float damageMult;
    private final ApparelComponent.ApparelSlot apparelSlot;
    private final List<Effect> hitEffects;

    public Limb(String name, float hitChance, float damageMult, ApparelComponent.ApparelSlot apparelSlot, List<Effect> hitEffects) {
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

    public ApparelComponent.ApparelSlot getApparelSlot() {
        return apparelSlot;
    }

    public void applyEffects(Actor target) {
        for(Effect effect : hitEffects) {
            target.effectComponent().addEffect(effect);
        }
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

}
