package com.github.finley243.adventureengine.effect;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.stat.MutableStatHolder;
import com.github.finley243.adventureengine.stat.StatInt;

public class EffectStatModInt extends Effect {

    private final String stat;
    private final int amount;

    public EffectStatModInt(Game game, String ID, int duration, boolean manualRemoval, boolean stackable, Condition conditionAdd, Condition conditionRemove, Condition conditionActive, Script scriptAdd, Script scriptRemove, Script scriptRound, String stat, int amount) {
        super(game, ID, duration, manualRemoval, stackable, conditionAdd, conditionRemove, conditionActive, scriptAdd, scriptRemove, scriptRound);
        this.stat = stat;
        this.amount = amount;
    }

    @Override
    public void start(MutableStatHolder target) {
        StatInt statInt = target.getStatInt(stat);
        if(statInt != null) {
            statInt.addMod(amount);
        }
    }

    @Override
    public void end(MutableStatHolder target) {
        StatInt statInt = target.getStatInt(stat);
        if(statInt != null) {
            statInt.addMod(-amount);
        }
    }

}
