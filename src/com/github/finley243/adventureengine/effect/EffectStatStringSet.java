package com.github.finley243.adventureengine.effect;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.stat.EffectableStatHolder;
import com.github.finley243.adventureengine.stat.StatStringSet;

import java.util.Set;

public class EffectStatStringSet extends Effect {

    private final String stat;
    private final Set<String> valuesAdd;
    private final Set<String> valuesRemove;

    public EffectStatStringSet(Game game, String ID, int duration, boolean manualRemoval, boolean stackable, Condition conditionAdd, Condition conditionRemove, Condition conditionActive, Script scriptAdd, Script scriptRemove, Script scriptRound, String stat, Set<String> valuesAdd, Set<String> valuesRemove) {
        super(game, ID, duration, manualRemoval, stackable, conditionAdd, conditionRemove, conditionActive, scriptAdd, scriptRemove, scriptRound);
        this.stat = stat;
        this.valuesAdd = valuesAdd;
        this.valuesRemove = valuesRemove;
    }

    @Override
    public void start(EffectableStatHolder target) {
        StatStringSet moddableSet = target.getStatStringSet(stat);
        moddableSet.addAdditional(valuesAdd);
        moddableSet.addCancellation(valuesRemove);
    }

    @Override
    public void end(EffectableStatHolder target) {
        StatStringSet moddableSet = target.getStatStringSet(stat);
        moddableSet.removeAdditional(valuesAdd);
        moddableSet.removeCancellation(valuesRemove);
    }

    @Override
    public void eachRound(EffectableStatHolder target) {}

}
