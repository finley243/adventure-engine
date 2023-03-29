package com.github.finley243.adventureengine.effect;

import com.github.finley243.adventureengine.stat.EffectableStatHolder;

import java.util.List;

public class EffectCompound extends Effect {

    private final List<Effect> subEffects;

    public EffectCompound(int duration, boolean manualRemoval, boolean stackable, List<Effect> subEffects) {
        super(duration, manualRemoval, stackable);
        this.subEffects = subEffects;
    }

    @Override
    public void start(EffectableStatHolder target) {
        for (Effect subEffect : subEffects) {
            subEffect.start(target);
        }
    }

    @Override
    public void end(EffectableStatHolder target) {
        for (Effect subEffect : subEffects) {
            subEffect.end(target);
        }
    }

    @Override
    public void eachRound(EffectableStatHolder target) {
        for (Effect subEffect : subEffects) {
            subEffect.eachRound(target);
        }
    }
}
