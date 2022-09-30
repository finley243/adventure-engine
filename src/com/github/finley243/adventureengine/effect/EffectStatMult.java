package com.github.finley243.adventureengine.effect;

import com.github.finley243.adventureengine.stat.StatHolder;
import com.github.finley243.adventureengine.stat.StatFloat;
import com.github.finley243.adventureengine.stat.StatInt;

public class EffectStatMult extends Effect {

    private final String stat;
    private final float amount;

    public EffectStatMult(int duration, boolean manualRemoval, boolean stackable, String stat, float amount) {
        super(duration, manualRemoval, stackable);
        this.stat = stat;
        this.amount = amount;
    }

    @Override
    public void start(StatHolder target) {
        StatInt statInt = target.getStatInt(stat);
        if(statInt != null) {
            statInt.addMult(amount);
        } else {
            StatFloat statFloat = target.getStatFloat(stat);
            if(statFloat != null) {
                statFloat.addMult(amount);
            }
        }
    }

    @Override
    public void end(StatHolder target) {
        StatInt statInt = target.getStatInt(stat);
        if(statInt != null) {
            statInt.addMult(-amount);
        } else {
            StatFloat statFloat = target.getStatFloat(stat);
            if(statFloat != null) {
                statFloat.addMult(-amount);
            }
        }
    }

    @Override
    public void eachTurn(StatHolder target) {

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
