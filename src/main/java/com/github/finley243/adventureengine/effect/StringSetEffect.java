package com.github.finley243.adventureengine.effect;

import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.load.GameDataException;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.stat.Stat;
import com.github.finley243.adventureengine.stat.StatHolder;
import com.github.finley243.adventureengine.stat.StringSetStat;

import java.util.Set;

public class StringSetEffect extends Effect {

    private final String stat;
    private final Set<String> valuesAdd;
    private final Set<String> valuesRemove;
    private final Condition statCondition;

    public StringSetEffect(String ID, int duration, boolean manualRemoval, boolean stackable, Condition conditionAdd, Condition conditionRemove, Condition conditionActive, Script scriptAdd, Script scriptRemove, Script scriptRound, String stat, Set<String> valuesAdd, Set<String> valuesRemove, Condition statCondition) {
        super(ID, duration, manualRemoval, stackable, conditionAdd, conditionRemove, conditionActive, scriptAdd, scriptRemove, scriptRound);
        this.stat = stat;
        this.valuesAdd = valuesAdd;
        this.valuesRemove = valuesRemove;
        this.statCondition = statCondition;
    }

    @Override
    public void start(StatHolder target) {
        Stat statObject = target.getStat(stat);
        if (statObject == null) throw new GameDataException("Invalid stat on target holder");
        if (!(statObject instanceof StringSetStat stringSetStat)) throw new GameDataException("Stat on target holder is not a string set");
        stringSetStat.addMod(new StringSetStat.StatStringSetMod(statCondition, valuesAdd, valuesRemove));
    }

    @Override
    public void end(StatHolder target) {
        Stat statObject = target.getStat(stat);
        if (statObject == null) throw new GameDataException("Invalid stat on target holder");
        if (!(statObject instanceof StringSetStat stringSetStat)) throw new GameDataException("Stat on target holder is not a string set");
        stringSetStat.removeMod(new StringSetStat.StatStringSetMod(statCondition, valuesAdd, valuesRemove));
    }

}
