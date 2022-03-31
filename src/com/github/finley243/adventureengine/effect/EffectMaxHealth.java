package com.github.finley243.adventureengine.effect;

import com.github.finley243.adventureengine.actor.Actor;

public class EffectMaxHealth extends Effect {

    private final int amount;

    public EffectMaxHealth(int duration, boolean manualRemoval, int amount) {
        super(duration, manualRemoval);
        this.amount = amount;
    }

    @Override
    public void start(Actor target) {
        target.getMaxHP().addMod(amount);
    }

    @Override
    public void end(Actor target) {
        target.getMaxHP().addMod(-amount);
    }

    @Override
    public void eachTurn(Actor target) {

    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o) && amount == ((EffectMaxHealth) o).amount;
    }

    @Override
    public int hashCode() {
        return (31 * super.hashCode()) + amount;
    }

}
