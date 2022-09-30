package com.github.finley243.adventureengine.effect;

import com.github.finley243.adventureengine.stat.StatHolder;
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
    public void start(StatHolder target) {
        StatInt statInt = target.getStatInt(stat);
        if(statInt != null) {
            statInt.addMod(amount);
        }
    }

    @Override
    public void end(StatHolder target) {
        StatInt statInt = target.getStatInt(stat);
        if(statInt != null) {
            statInt.addMod(-amount);
        }
    }

    @Override
    public void eachTurn(StatHolder target) {

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
