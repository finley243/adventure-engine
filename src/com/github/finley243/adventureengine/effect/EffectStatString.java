package com.github.finley243.adventureengine.effect;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.stat.EffectableStatHolder;

public class EffectStatString extends Effect {

    private final String stat;
    private final String value;

    public EffectStatString(Game game, String ID, int duration, boolean manualRemoval, boolean stackable, Condition conditionAdd, Condition conditionRemove, Condition conditionActive, Script scriptAdd, Script scriptRemove, Script scriptRound, String stat, String value) {
        super(game, ID, duration, manualRemoval, stackable, conditionAdd, conditionRemove, conditionActive, scriptAdd, scriptRemove, scriptRound);
        this.stat = stat;
        this.value = value;
    }

    @Override
    public void start(EffectableStatHolder target) {
        target.getStatString(stat).addMod(value);
    }

    @Override
    public void end(EffectableStatHolder target) {
        target.getStatString(stat).removeMod(value);
    }

    @Override
    public void eachRound(EffectableStatHolder target) {}

}
