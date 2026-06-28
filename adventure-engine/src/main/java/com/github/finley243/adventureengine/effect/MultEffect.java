package com.github.finley243.adventureengine.effect;

import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.load.GameDataException;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.stat.FloatStat;
import com.github.finley243.adventureengine.stat.IntStat;
import com.github.finley243.adventureengine.stat.Stat;
import com.github.finley243.adventureengine.stat.StatHolder;

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
    public void start(StatHolder target) {
        Stat statObject = target.getStat(stat);
        switch (statObject) {
            case null -> throw new GameDataException("Invalid stat on target holder");
            case IntStat intStat -> intStat.addMod(new IntStat.StatIntMod(statCondition, 0, amount));
            case FloatStat floatStat -> floatStat.addMod(new FloatStat.StatFloatMod(statCondition, 0.0f, amount));
            default -> throw new GameDataException("Stat on target holder is not an integer or float");
        }
    }

    @Override
    public void end(StatHolder target) {
        Stat statObject = target.getStat(stat);
        switch (statObject) {
            case null -> throw new GameDataException("Invalid stat on target holder");
            case IntStat intStat -> intStat.removeMod(new IntStat.StatIntMod(statCondition, 0, amount));
            case FloatStat floatStat -> floatStat.removeMod(new FloatStat.StatFloatMod(statCondition, 0.0f, amount));
            default -> throw new GameDataException("Stat on target holder is not an integer or float");
        }
    }

}
