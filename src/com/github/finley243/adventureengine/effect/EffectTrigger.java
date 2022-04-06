package com.github.finley243.adventureengine.effect;

import com.github.finley243.adventureengine.actor.Actor;

public class EffectTrigger extends Effect {

    private final String trigger;

    public EffectTrigger(int duration, boolean manualRemoval, String trigger) {
        super(duration, manualRemoval, 0);
        this.trigger = trigger;
    }

    @Override
    public void start(Actor target) {
        target.triggerSpecial(trigger);
    }

    @Override
    public void end(Actor target) {

    }

    @Override
    public void eachTurn(Actor target) {
        target.triggerSpecial(trigger);
    }

}
