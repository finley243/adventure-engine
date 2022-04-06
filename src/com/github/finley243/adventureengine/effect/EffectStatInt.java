package com.github.finley243.adventureengine.effect;

import com.github.finley243.adventureengine.ModdableStatInt;
import com.github.finley243.adventureengine.actor.Actor;

public class EffectStatInt extends Effect {

    private final String stat;

    public EffectStatInt(int duration, boolean manualRemoval, int amount, String stat) {
        super(duration, manualRemoval, amount);
        this.stat = stat;
    }

    @Override
    public void start(Actor target) {
        ModdableStatInt moddableStatInt = target.getStatInt(stat);
        if(moddableStatInt != null) {
            moddableStatInt.addMod(amount);
        }
    }

    @Override
    public void end(Actor target) {
        ModdableStatInt moddableStatInt = target.getStatInt(stat);
        if(moddableStatInt != null) {
            moddableStatInt.addMod(-amount);
        }
    }

    @Override
    public void eachTurn(Actor target) {

    }

}
