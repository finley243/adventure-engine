package com.github.finley243.adventureengine.effect;

import com.github.finley243.adventureengine.ModdableStatFloat;
import com.github.finley243.adventureengine.actor.Actor;

public class EffectStatFloat extends Effect {

    private final String stat;
    private final float amountFloat;

    public EffectStatFloat(int duration, boolean manualRemoval, float amount, String stat) {
        super(duration, manualRemoval, 0);
        this.stat = stat;
        this.amountFloat = amount;
    }

    @Override
    public void start(Actor target) {
        ModdableStatFloat moddableStatFloat = target.getStatFloat(stat);
        if(moddableStatFloat != null) {
            moddableStatFloat.addMod(amount);
        }
    }

    @Override
    public void end(Actor target) {
        ModdableStatFloat moddableStatFloat = target.getStatFloat(stat);
        if(moddableStatFloat != null) {
            moddableStatFloat.addMod(-amount);
        }
    }

    @Override
    public void eachTurn(Actor target) {

    }

}
