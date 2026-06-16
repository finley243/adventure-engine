package com.github.finley243.adventureengine.effect;

import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.load.GameDataException;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.stat.Stat;
import com.github.finley243.adventureengine.stat.StatHolder;
import com.github.finley243.adventureengine.stat.StringStat;

public class StringEffect extends Effect {

    private final String stat;
    private final String value;
    private final Condition statCondition;

    public StringEffect(String ID, int duration, boolean manualRemoval, boolean stackable, Condition conditionAdd, Condition conditionRemove, Condition conditionActive, Script scriptAdd, Script scriptRemove, Script scriptRound, String stat, String value, Condition statCondition) {
        super(ID, duration, manualRemoval, stackable, conditionAdd, conditionRemove, conditionActive, scriptAdd, scriptRemove, scriptRound);
        this.stat = stat;
        this.value = value;
        this.statCondition = statCondition;
    }

    @Override
    public void start(StatHolder target) {
        Stat statObject = target.getStat(stat);
        if (statObject == null) throw new GameDataException("Invalid stat on target holder");
        if (!(statObject instanceof StringStat stringStat)) throw new GameDataException("Stat on target holder is not a string");
        stringStat.addMod(new StringStat.StatStringMod(statCondition, value));
    }

    @Override
    public void end(StatHolder target) {
        Stat statObject = target.getStat(stat);
        if (statObject == null) throw new GameDataException("Invalid stat on target holder");
        if (!(statObject instanceof StringStat stringStat)) throw new GameDataException("Stat on target holder is not a string");
        stringStat.removeMod(new StringStat.StatStringMod(statCondition, value));
    }

}
