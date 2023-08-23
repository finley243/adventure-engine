package com.github.finley243.adventureengine.effect;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.stat.MutableStatController;
import com.github.finley243.adventureengine.stat.MutableStatHolder;
import com.github.finley243.adventureengine.stat.StatBoolean;

public class EffectStatBoolean extends Effect {

    private final String stat;
    private final boolean value;
    private final Condition statCondition;

    public EffectStatBoolean(Game game, String ID, int duration, boolean manualRemoval, boolean stackable, Condition conditionAdd, Condition conditionRemove, Condition conditionActive, Script scriptAdd, Script scriptRemove, Script scriptRound, String stat, boolean value, Condition statCondition) {
        super(game, ID, duration, manualRemoval, stackable, conditionAdd, conditionRemove, conditionActive, scriptAdd, scriptRemove, scriptRound);
        this.stat = stat;
        this.value = value;
        this.statCondition = statCondition;
    }

    @Override
    public void start(MutableStatController controller) {
        controller.getStatBoolean(stat).addMod(new StatBoolean.StatBooleanMod(statCondition, value));
    }

    @Override
    public void end(MutableStatController controller) {
        controller.getStatBoolean(stat).removeMod(new StatBoolean.StatBooleanMod(statCondition, value));
    }

}
