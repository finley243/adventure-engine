package com.github.finley243.adventureengine.effect;

import com.github.finley243.adventureengine.effect.moddable.Moddable;

public class EffectStatBoolean extends Effect {

    private final String stat;
    private final boolean value;

    public EffectStatBoolean(int duration, boolean manualRemoval, boolean stackable, String stat, boolean value) {
        super(duration, manualRemoval, stackable);
        this.stat = stat;
        this.value = value;
    }

    @Override
    public void start(Moddable target) {
        target.getStatBoolean(stat).addMod(value);
        target.onStatChange();
    }

    @Override
    public void end(Moddable target) {
        target.getStatBoolean(stat).removeMod(value);
        target.onStatChange();
    }

    @Override
    public void eachTurn(Moddable target) {

    }

}
