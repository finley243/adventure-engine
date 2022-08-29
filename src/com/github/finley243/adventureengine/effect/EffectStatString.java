package com.github.finley243.adventureengine.effect;

import com.github.finley243.adventureengine.effect.moddable.Moddable;

public class EffectStatString extends Effect {

    private final String stat;
    private final String value;

    public EffectStatString(int duration, boolean manualRemoval, boolean stackable, String stat, String value) {
        super(duration, manualRemoval, stackable);
        this.stat = stat;
        this.value = value;
    }

    @Override
    public void start(Moddable target) {
        target.getStatString(stat).addMod(value);
    }

    @Override
    public void end(Moddable target) {
        target.getStatString(stat).removeMod(value);
    }

    @Override
    public void eachTurn(Moddable target) {}

}
