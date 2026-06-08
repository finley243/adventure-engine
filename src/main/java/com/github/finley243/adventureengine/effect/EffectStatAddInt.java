package com.github.finley243.adventureengine.effect;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.stat.MutableStatHolder;
import com.github.finley243.adventureengine.stat.StatInt;

public class EffectStatAddInt extends Effect {

    private final String stat;
    private final int amount;
    private final Condition statCondition;

    public EffectStatAddInt(Game game, String ID, int duration, boolean manualRemoval, boolean stackable, Condition conditionAdd, Condition conditionRemove, Condition conditionActive, Script scriptAdd, Script scriptRemove, Script scriptRound, String stat, int amount, Condition statCondition) {
        super(game, ID, duration, manualRemoval, stackable, conditionAdd, conditionRemove, conditionActive, scriptAdd, scriptRemove, scriptRound);
        this.stat = stat;
        this.amount = amount;
        this.statCondition = statCondition;
    }

    @Override
    public void start(Game game, MutableStatHolder target) {
        StatInt statInt = target.getStatInt(game, stat);
        if(statInt != null) {
            statInt.addMod(game, new StatInt.StatIntMod(statCondition, amount, 0.0f));
        }
    }

    @Override
    public void end(Game game, MutableStatHolder target) {
        StatInt statInt = target.getStatInt(game, stat);
        if(statInt != null) {
            statInt.removeMod(game, new StatInt.StatIntMod(statCondition, amount, 0.0f));
        }
    }

}
