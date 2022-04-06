package com.github.finley243.adventureengine.effect;

import com.github.finley243.adventureengine.ModdableStatInt;
import com.github.finley243.adventureengine.actor.Actor;

public class EffectStatModInt extends Effect {

    private final String stat;
    private final int amount;

    public EffectStatModInt(int duration, boolean manualRemoval, String stat, int amount) {
        super(duration, manualRemoval);
        this.stat = stat;
        this.amount = amount;
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
