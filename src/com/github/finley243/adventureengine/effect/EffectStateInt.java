package com.github.finley243.adventureengine.effect;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.stat.EffectableStatHolder;

public class EffectStateInt extends Effect {

    private final String state;
    private final int amount;

    public EffectStateInt(Game game, String ID, int duration, boolean manualRemoval, boolean stackable, Condition conditionAdd, Condition conditionRemove, Condition conditionActive, Script scriptAdd, Script scriptRemove, Script scriptRound, String state, int amount) {
        super(game, ID, duration, manualRemoval, stackable, conditionAdd, conditionRemove, conditionActive, scriptAdd, scriptRemove, scriptRound);
        this.state = state;
        this.amount = amount;
    }

    @Override
    public void start(EffectableStatHolder target) {
        target.modStateInteger(state, amount);
    }

    @Override
    public void end(EffectableStatHolder target) {

    }

    @Override
    public void eachRound(EffectableStatHolder target) {
        target.modStateInteger(state, amount);
    }

}
