package com.github.finley243.adventureengine.effect;

import com.github.finley243.adventureengine.actor.Actor;

public class EffectStateInt extends Effect {

    private final String state;
    private final int amount;

    public EffectStateInt(int duration, boolean manualRemoval, String state, int amount) {
        super(duration, manualRemoval);
        this.state = state;
        this.amount = amount;
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
