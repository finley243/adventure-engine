package com.github.finley243.adventureengine.effect;

import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.load.GameDataException;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.stat.BooleanStat;
import com.github.finley243.adventureengine.stat.Stat;
import com.github.finley243.adventureengine.stat.StatHolder;

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
    public void start(StatHolder target) {
        Stat statObject = target.getStat(stat);
        if (statObject == null) throw new GameDataException("Invalid stat on target holder");
        if (!(statObject instanceof BooleanStat booleanStat)) throw new GameDataException("Stat on target holder is not a boolean");
        booleanStat.addMod(new BooleanStat.StatBooleanMod(statCondition, value));
    }

    @Override
    public void end(StatHolder target) {
        Stat statObject = target.getStat(stat);
        if (statObject == null) throw new GameDataException("Invalid stat on target holder");
        if (!(statObject instanceof BooleanStat booleanStat)) throw new GameDataException("Stat on target holder is not a boolean");
        booleanStat.removeMod(new BooleanStat.StatBooleanMod(statCondition, value));
    }

}
