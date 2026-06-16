package com.github.finley243.adventureengine.effect;

import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.load.GameDataException;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.stat.Stat;
import com.github.finley243.adventureengine.stat.StatHolder;
import com.github.finley243.adventureengine.stat.IntStat;

public class AddIntEffect extends Effect {

    private final String stat;
    private final int amount;
    private final Condition statCondition;

    public AddIntEffect(String ID, int duration, boolean manualRemoval, boolean stackable, Condition conditionAdd, Condition conditionRemove, Condition conditionActive, Script scriptAdd, Script scriptRemove, Script scriptRound, String stat, int amount, Condition statCondition) {
        super(ID, duration, manualRemoval, stackable, conditionAdd, conditionRemove, conditionActive, scriptAdd, scriptRemove, scriptRound);
        this.stat = stat;
        this.amount = amount;
        this.statCondition = statCondition;
    }

    @Override
    public void start(StatHolder target) {
        Stat statObject = target.getStat(stat);
        if (statObject == null) throw new GameDataException("Invalid stat on target holder");
        if (!(statObject instanceof IntStat intStat)) throw new GameDataException("Stat on target holder is not an integer");
        intStat.addMod(new IntStat.StatIntMod(statCondition, amount, 0.0f));
    }

    @Override
    public void end(StatHolder target) {
        Stat statObject = target.getStat(stat);
        if (statObject == null) throw new GameDataException("Invalid stat on target holder");
        if (!(statObject instanceof IntStat intStat)) throw new GameDataException("Stat on target holder is not an integer");
        intStat.removeMod(new IntStat.StatIntMod(statCondition, amount, 0.0f));
    }

}
