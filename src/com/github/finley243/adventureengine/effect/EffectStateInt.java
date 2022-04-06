package com.github.finley243.adventureengine.effect;

import com.github.finley243.adventureengine.actor.Actor;

public class EffectStateInt extends Effect {

    private final String state;

    public EffectStateInt(int duration, boolean manualRemoval, int amount, String state) {
        super(duration, manualRemoval, amount);
        this.state = state;
    }

    @Override
    public void start(Actor target) {
        target.modifyState(state, amount);
    }

    @Override
    public void end(Actor target) {

    }

    @Override
    public void eachTurn(Actor target) {
        target.modifyState(state, amount);
    }

}
