package com.github.finley243.adventureengine.effect;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.stat.EffectableStatHolder;

public class EffectTrigger extends Effect {

    private final String trigger;

    public EffectTrigger(Game game, String ID, int duration, boolean manualRemoval, boolean stackable, String trigger) {
        super(game, ID, duration, manualRemoval, stackable);
        this.trigger = trigger;
    }

    @Override
    public void start(EffectableStatHolder target) {
        target.triggerEffect(trigger);
    }

    @Override
    public void end(EffectableStatHolder target) {

    }

    @Override
    public void eachRound(EffectableStatHolder target) {
        target.triggerEffect(trigger);
    }

}
