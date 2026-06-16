package com.github.finley243.adventureengine.effect;

import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.stat.MutableStatHolder;
import com.github.finley243.adventureengine.stat.FloatStat;

public class AddFloatEffect extends Effect {

    private final String stat;
    private final float amount;
    private final Condition statCondition;

    public AddFloatEffect(String ID, int duration, boolean manualRemoval, boolean stackable, Condition conditionAdd, Condition conditionRemove, Condition conditionActive, Script scriptAdd, Script scriptRemove, Script scriptRound, String stat, float amount, Condition statCondition) {
        super(ID, duration, manualRemoval, stackable, conditionAdd, conditionRemove, conditionActive, scriptAdd, scriptRemove, scriptRound);
        this.stat = stat;
        this.amount = amount;
        this.statCondition = statCondition;
    }

    @Override
    public void start(MutableStatHolder target) {
        FloatStat floatStat = target.getStatFloat(stat);
        if(floatStat != null) {
            floatStat.addMod(new FloatStat.StatFloatMod(statCondition, amount, 0.0f));
        }
    }

    @Override
    public void end(MutableStatHolder target) {
        FloatStat floatStat = target.getStatFloat(stat);
        if(floatStat != null) {
            floatStat.removeMod(new FloatStat.StatFloatMod(statCondition, amount, 0.0f));
        }
    }

}
