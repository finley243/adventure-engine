package com.github.finley243.adventureengine.effect;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.stat.MutableStatHolder;
import com.github.finley243.adventureengine.stat.StatFloat;

public class EffectStatAddFloat extends Effect {

    private final String stat;
    private final float amount;
    private final Condition statCondition;

    public EffectStatAddFloat(Game game, String ID, int duration, boolean manualRemoval, boolean stackable, Condition conditionAdd, Condition conditionRemove, Condition conditionActive, Script scriptAdd, Script scriptRemove, Script scriptRound, String stat, float amount, Condition statCondition) {
        super(game, ID, duration, manualRemoval, stackable, conditionAdd, conditionRemove, conditionActive, scriptAdd, scriptRemove, scriptRound);
        this.stat = stat;
        this.amount = amount;
        this.statCondition = statCondition;
    }

    @Override
    public void start(MutableStatHolder target) {
        StatFloat statFloat = target.getStatFloat(stat);
        if(statFloat != null) {
            statFloat.addMod(new StatFloat.StatFloatMod(statCondition, amount, 0.0f));
        }
    }

    @Override
    public void end(MutableStatHolder target) {
        StatFloat statFloat = target.getStatFloat(stat);
        if(statFloat != null) {
            statFloat.removeMod(new StatFloat.StatFloatMod(statCondition, amount, 0.0f));
        }
    }

}
