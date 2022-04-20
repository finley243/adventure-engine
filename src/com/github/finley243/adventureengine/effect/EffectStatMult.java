package com.github.finley243.adventureengine.effect;

import com.github.finley243.adventureengine.effect.moddable.ModdableStatFloat;
import com.github.finley243.adventureengine.effect.moddable.ModdableStatInt;
import com.github.finley243.adventureengine.actor.Actor;

public class EffectStatMult extends Effect {

    private final String stat;
    private final boolean isFloat;
    private final float amount;

    public EffectStatMult(int duration, boolean manualRemoval, String stat, boolean isFloat, float amount) {
        super(duration, manualRemoval);
        this.stat = stat;
        this.isFloat = isFloat;
        this.amount = amount;
    }

    @Override
    public void start(Actor target) {
        if(isFloat) {
            ModdableStatFloat moddableStatFloat = target.getStatFloat(stat);
            if(moddableStatFloat != null) {
                moddableStatFloat.addMult(amount);
            }
        } else {
            ModdableStatInt moddableStatInt = target.getStatInt(stat);
            if(moddableStatInt != null) {
                moddableStatInt.addMult(amount);
            }
        }
    }

    @Override
    public void end(Actor target) {
        if(isFloat) {
            ModdableStatFloat moddableStatFloat = target.getStatFloat(stat);
            if(moddableStatFloat != null) {
                moddableStatFloat.addMult(-amount);
            }
        } else {
            ModdableStatInt moddableStatInt = target.getStatInt(stat);
            if(moddableStatInt != null) {
                moddableStatInt.addMult(-amount);
            }
        }
    }

    @Override
    public void eachTurn(Actor target) {

    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o) && stat.equals(((EffectStatMult) o).stat) && isFloat == (((EffectStatMult) o).isFloat) && amount == ((EffectStatMult) o).amount;
    }

    @Override
    public int hashCode() {
        return 31 * (31 * ((31 * super.hashCode()) + stat.hashCode()) + Boolean.hashCode(isFloat)) + Float.hashCode(amount);
    }

}
