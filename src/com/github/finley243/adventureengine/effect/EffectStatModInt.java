package com.github.finley243.adventureengine.effect;

import com.github.finley243.adventureengine.stat.EffectableStatHolder;
import com.github.finley243.adventureengine.stat.StatInt;

public class EffectStatModInt extends Effect {

    private final String stat;
    private final int amount;

    public EffectStatModInt(int duration, boolean manualRemoval, boolean stackable, String stat, int amount) {
        super(duration, manualRemoval, stackable);
        this.stat = stat;
        this.amount = amount;
    }

    @Override
    public void start(EffectableStatHolder target) {
        StatInt statInt = target.getStatInt(stat);
        if(statInt != null) {
            statInt.addMod(amount);
        }
    }

    @Override
    public void end(EffectableStatHolder target) {
        StatInt statInt = target.getStatInt(stat);
        if(statInt != null) {
            statInt.addMod(-amount);
        }
    }

    @Override
    public void eachRound(EffectableStatHolder target) {

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
