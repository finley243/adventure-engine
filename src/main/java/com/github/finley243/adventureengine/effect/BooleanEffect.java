package com.github.finley243.adventureengine.effect;

import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.stat.MutableStatHolder;
import com.github.finley243.adventureengine.stat.BooleanStat;

public class BooleanEffect extends Effect {

    private final String stat;
    private final boolean value;
    private final Condition statCondition;

    public BooleanEffect(String ID, int duration, boolean manualRemoval, boolean stackable, Condition conditionAdd, Condition conditionRemove, Condition conditionActive, Script scriptAdd, Script scriptRemove, Script scriptRound, String stat, boolean value, Condition statCondition) {
        super(ID, duration, manualRemoval, stackable, conditionAdd, conditionRemove, conditionActive, scriptAdd, scriptRemove, scriptRound);
        this.stat = stat;
        this.value = value;
        this.statCondition = statCondition;
    }

    @Override
    public void start(MutableStatHolder target) {
        target.getStatBoolean(stat).addMod(new BooleanStat.StatBooleanMod(statCondition, value));
    }

    @Override
    public void end(MutableStatHolder target) {
        target.getStatBoolean(stat).removeMod(new BooleanStat.StatBooleanMod(statCondition, value));
    }

}
