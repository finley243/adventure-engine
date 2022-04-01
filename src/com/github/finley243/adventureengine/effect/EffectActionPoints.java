package com.github.finley243.adventureengine.effect;

import com.github.finley243.adventureengine.actor.Actor;

public class EffectActionPoints extends Effect {

    public EffectActionPoints(int duration, boolean manualRemoval, int amount) {
        super(duration, manualRemoval, amount);
    }

    @Override
    public void start(Actor target) {
        target.getActionPoints().addMod(amount);
    }

    @Override
    public void end(Actor target) {
        target.getActionPoints().addMod(-amount);
    }

    @Override
    public void eachTurn(Actor target) {}

}
