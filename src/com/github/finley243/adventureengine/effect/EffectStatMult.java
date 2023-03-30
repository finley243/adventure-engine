package com.github.finley243.adventureengine.effect;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.stat.EffectableStatHolder;
import com.github.finley243.adventureengine.stat.StatFloat;
import com.github.finley243.adventureengine.stat.StatInt;

public class EffectStatMult extends Effect {

    private final String stat;
    private final float amount;

    public EffectStatMult(Game game, String ID, int duration, boolean manualRemoval, boolean stackable, Condition conditionAdd, Condition conditionRemove, Condition conditionActive, Script scriptAdd, Script scriptRemove, Script scriptRound, String stat, float amount) {
        super(game, ID, duration, manualRemoval, stackable, conditionAdd, conditionRemove, conditionActive, scriptAdd, scriptRemove, scriptRound);
        this.stat = stat;
        this.amount = amount;
    }

    @Override
    public void start(EffectableStatHolder target) {
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
    public void end(EffectableStatHolder target) {
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
    public void eachRound(EffectableStatHolder target) {

    }

}
