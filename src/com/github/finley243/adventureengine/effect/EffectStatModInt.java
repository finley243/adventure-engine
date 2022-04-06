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

    @Override
    public boolean equals(Object o) {
        return super.equals(o) && stat.equals(((EffectStatModInt) o).stat) && amount == ((EffectStatModInt) o).amount;
    }

    @Override
    public int hashCode() {
        return 31 * ((31 * super.hashCode()) + stat.hashCode()) + amount;
    }

}
