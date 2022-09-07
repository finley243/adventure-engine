package com.github.finley243.adventureengine.effect;

import com.github.finley243.adventureengine.effect.moddable.Moddable;
import com.github.finley243.adventureengine.effect.moddable.ModdableStringSet;

import java.util.Set;

public class EffectStringSet extends Effect {

    private final String stat;
    private final Set<String> valuesAdd;
    private final Set<String> valuesRemove;

    public EffectStringSet(int duration, boolean manualRemoval, boolean stackable, String stat, Set<String> valuesAdd, Set<String> valuesRemove) {
        super(duration, manualRemoval, stackable);
        this.stat = stat;
        this.valuesAdd = valuesAdd;
        this.valuesRemove = valuesRemove;
    }

    @Override
    public void start(Moddable target) {
        ModdableStringSet moddableSet = target.getStatStrings(stat);
        moddableSet.addAdditional(valuesAdd);
        moddableSet.addCancellation(valuesRemove);
    }

    @Override
    public void end(Moddable target) {
        ModdableStringSet moddableSet = target.getStatStrings(stat);
        moddableSet.removeAdditional(valuesAdd);
        moddableSet.removeCancellation(valuesRemove);
    }

    @Override
    public void eachTurn(Moddable target) {}

}
