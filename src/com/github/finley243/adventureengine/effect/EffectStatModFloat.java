package com.github.finley243.adventureengine.effect;

import com.github.finley243.adventureengine.effect.moddable.Moddable;
import com.github.finley243.adventureengine.effect.moddable.ModdableStatFloat;

public class EffectStatModFloat extends Effect {

    private final String stat;
    private final float amount;

    public EffectStatModFloat(int duration, boolean manualRemoval, boolean stackable, String stat, float amount) {
        super(duration, manualRemoval, stackable);
        this.stat = stat;
        this.amount = amount;
    }

    @Override
    public void start(Moddable target) {
        ModdableStatFloat moddableStatFloat = target.getStatFloat(stat);
        if(moddableStatFloat != null) {
            moddableStatFloat.addMod(amount);
        }
    }

    @Override
    public void end(Moddable target) {
        ModdableStatFloat moddableStatFloat = target.getStatFloat(stat);
        if(moddableStatFloat != null) {
            moddableStatFloat.addMod(-amount);
        }
    }

    @Override
    public void eachTurn(Moddable target) {

    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o) && stat.equals(((EffectStatModFloat) o).stat) && amount == ((EffectStatModFloat) o).amount;
    }

    @Override
    public int hashCode() {
        return 31 * ((31 * super.hashCode()) + stat.hashCode()) + Float.hashCode(amount);
    }

}
