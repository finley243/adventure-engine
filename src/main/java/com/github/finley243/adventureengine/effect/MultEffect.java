package com.github.finley243.adventureengine.effect;

import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.stat.MutableStatHolder;
import com.github.finley243.adventureengine.stat.FloatStat;
import com.github.finley243.adventureengine.stat.IntStat;

public class MultEffect extends Effect {

    private final String stat;
    private final float amount;
    private final Condition statCondition;

    public MultEffect(String ID, int duration, boolean manualRemoval, boolean stackable, Condition conditionAdd, Condition conditionRemove, Condition conditionActive, Script scriptAdd, Script scriptRemove, Script scriptRound, String stat, float amount, Condition statCondition) {
        super(ID, duration, manualRemoval, stackable, conditionAdd, conditionRemove, conditionActive, scriptAdd, scriptRemove, scriptRound);
        this.stat = stat;
        this.amount = amount;
        this.statCondition = statCondition;
    }

    @Override
    public void start(MutableStatHolder target) {
        IntStat intStat = target.getStatInt(stat);
        if(intStat != null) {
            intStat.addMod(new IntStat.StatIntMod(statCondition, 0, amount));
        } else {
            FloatStat floatStat = target.getStatFloat(stat);
            if(floatStat != null) {
                floatStat.addMod(new FloatStat.StatFloatMod(statCondition, 0.0f, amount));
            }
        }
    }

    @Override
    public void end(MutableStatHolder target) {
        IntStat intStat = target.getStatInt(stat);
        if(intStat != null) {
            intStat.removeMod(new IntStat.StatIntMod(statCondition, 0, amount));
        } else {
            FloatStat floatStat = target.getStatFloat(stat);
            if(floatStat != null) {
                floatStat.removeMod(new FloatStat.StatFloatMod(statCondition, 0.0f, amount));
            }
        }
    }

}
