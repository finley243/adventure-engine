package com.github.finley243.adventureengine.effect;

import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.load.GameDataException;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.stat.FloatStat;
import com.github.finley243.adventureengine.stat.Stat;
import com.github.finley243.adventureengine.stat.StatHolder;

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
    public void start(StatHolder target) {
        Stat statObject = target.getStat(stat);
        if (statObject == null) throw new GameDataException("Invalid stat on target holder");
        if (!(statObject instanceof FloatStat floatStat)) throw new GameDataException("Stat on target holder is not a float");
        floatStat.addMod(new FloatStat.StatFloatMod(statCondition, amount, 0.0f));
    }

    @Override
    public void end(StatHolder target) {
        Stat statObject = target.getStat(stat);
        if (statObject == null) throw new GameDataException("Invalid stat on target holder");
        if (!(statObject instanceof FloatStat floatStat)) throw new GameDataException("Stat on target holder is not a float");
        floatStat.removeMod(new FloatStat.StatFloatMod(statCondition, amount, 0.0f));
    }

}
