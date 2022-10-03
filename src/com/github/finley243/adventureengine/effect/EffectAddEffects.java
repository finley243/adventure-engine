package com.github.finley243.adventureengine.effect;

import com.github.finley243.adventureengine.stat.StatHolder;

import java.util.List;

public class EffectAddEffects extends Effect {

    private final String stat;
    private final List<Effect> effects;

    public EffectAddEffects(int duration, boolean manualRemoval, boolean stackable, String stat, List<Effect> effects) {
        super(duration, manualRemoval, stackable);
        this.stat = stat;
        this.effects = effects;
    }

    @Override
    public void start(StatHolder target) {
        target.getStatEffects(stat).addAdditional(effects);
    }

    @Override
    public void end(StatHolder target) {
        target.getStatEffects(stat).removeAdditional(effects);
    }

    @Override
    public void eachRound(StatHolder target) {

    }

}
