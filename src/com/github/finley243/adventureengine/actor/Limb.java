package com.github.finley243.adventureengine.actor;

import com.github.finley243.adventureengine.effect.Effect;

import java.util.List;

public class Limb {

    private final String name;
    private final String meleeHitPhrase;
    private final String meleeCritHitPhrase;
    private final String meleeMissPhrase;
    private final String rangedHitPhrase;
    private final String rangedCritHitPhrase;
    private final String rangedMissPhrase;
    private final float hitChance;
    private final float damageMult;
    private final ApparelManager.ApparelSlot apparelSlot;
    private final List<Effect> hitEffects;

    public Limb(String name, String meleeHitPhrase, String meleeCritHitPhrase, String meleeMissPhrase, String rangedHitPhrase, String rangedCritHitPhrase, String rangedMissPhrase, float hitChance, float damageMult, ApparelManager.ApparelSlot apparelSlot, List<Effect> hitEffects) {
        this.name = name;
        this.meleeHitPhrase = meleeHitPhrase;
        this.meleeCritHitPhrase = meleeCritHitPhrase;
        this.meleeMissPhrase = meleeMissPhrase;
        this.rangedHitPhrase = rangedHitPhrase;
        this.rangedCritHitPhrase = rangedCritHitPhrase;
        this.rangedMissPhrase = rangedMissPhrase;
        this.hitChance = hitChance;
        this.damageMult = damageMult;
        this.apparelSlot = apparelSlot;
        this.hitEffects = hitEffects;
    }

    public String getName() {
        return name;
    }

    public String getMeleeHitPhrase() {
        return meleeHitPhrase;
    }

    public String getMeleeCritHitPhrase() {
        return meleeCritHitPhrase;
    }

    public String getMeleeMissPhrase() {
        return meleeMissPhrase;
    }

    public String getRangedHitPhrase() {
        return rangedHitPhrase;
    }

    public String getRangedCritHitPhrase() {
        return rangedCritHitPhrase;
    }

    public String getRangedMissPhrase() {
        return rangedMissPhrase;
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

    public void applyEffects(Actor target) {
        for(Effect effect : hitEffects) {
            target.addEffect(effect.generate());
        }
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

}
