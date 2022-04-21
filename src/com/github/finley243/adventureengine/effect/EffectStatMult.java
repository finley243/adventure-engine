package com.github.finley243.adventureengine.effect;

import com.github.finley243.adventureengine.effect.moddable.ModdableStatFloat;
import com.github.finley243.adventureengine.effect.moddable.ModdableStatInt;
import com.github.finley243.adventureengine.actor.Actor;

public class EffectStatMult extends Effect {

    private final String stat;
    private final float amount;

    public EffectStatMult(int duration, boolean manualRemoval, String stat, float amount) {
        super(duration, manualRemoval);
        this.stat = stat;
        this.amount = amount;
    }

    @Override
    public void start(Actor target) {
        ModdableStatInt moddableStatInt = target.getStatInt(stat);
        if(moddableStatInt != null) {
            moddableStatInt.addMult(amount);
        } else {
            ModdableStatFloat moddableStatFloat = target.getStatFloat(stat);
            if(moddableStatFloat != null) {
                moddableStatFloat.addMult(amount);
            }
        }
    }

    @Override
    public void end(Actor target) {
        ModdableStatInt moddableStatInt = target.getStatInt(stat);
        if(moddableStatInt != null) {
            moddableStatInt.addMult(-amount);
        } else {
            ModdableStatFloat moddableStatFloat = target.getStatFloat(stat);
            if(moddableStatFloat != null) {
                moddableStatFloat.addMult(-amount);
            }
        }
    }

    @Override
    public void eachTurn(Actor target) {

    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o) && stat.equals(((EffectStatMult) o).stat) && amount == ((EffectStatMult) o).amount;
    }

    @Override
    public int hashCode() {
        return 31 * ((31 * super.hashCode()) + stat.hashCode()) + Float.hashCode(amount);
    }

}
