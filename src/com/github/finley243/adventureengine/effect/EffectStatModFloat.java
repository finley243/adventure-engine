package com.github.finley243.adventureengine.effect;

import com.github.finley243.adventureengine.ModdableStatFloat;
import com.github.finley243.adventureengine.actor.Actor;

public class EffectStatModFloat extends Effect {

    private final String stat;
    private final float amount;

    public EffectStatModFloat(int duration, boolean manualRemoval, String stat, float amount) {
        super(duration, manualRemoval);
        this.stat = stat;
        this.amount = amount;
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
