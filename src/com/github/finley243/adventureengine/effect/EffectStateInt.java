package com.github.finley243.adventureengine.effect;

import com.github.finley243.adventureengine.effect.moddable.Moddable;

public class EffectStateInt extends Effect {

    private final String state;
    private final int amount;

    public EffectStateInt(int duration, boolean manualRemoval, boolean stackable, String state, int amount) {
        super(duration, manualRemoval, stackable);
        this.state = state;
        this.amount = amount;
    }

    @Override
    public void start(Moddable target) {
        target.modifyState(state, amount);
    }

    @Override
    public void end(Moddable target) {

    }

    @Override
    public void eachTurn(Moddable target) {
        target.modifyState(state, amount);
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o) && state.equals(((EffectStateInt) o).state) && amount == ((EffectStateInt) o).amount;
    }

    @Override
    public int hashCode() {
        return 31 * ((31 * super.hashCode()) + state.hashCode()) + amount;
    }

}
