package com.github.finley243.adventureengine.effect;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.stat.MutableStatHolder;

import java.util.List;

public class EffectCompound extends Effect {

    private final List<Effect> subEffects;

    public EffectCompound(Game game, String ID, int duration, boolean manualRemoval, boolean stackable, Condition conditionAdd, Condition conditionRemove, Condition conditionActive, Script scriptAdd, Script scriptRemove, Script scriptRound, List<Effect> subEffects) {
        super(game, ID, duration, manualRemoval, stackable, conditionAdd, conditionRemove, conditionActive, scriptAdd, scriptRemove, scriptRound);
        this.subEffects = subEffects;
    }

    @Override
    public void start(Game game, MutableStatHolder target) {
        for (Effect subEffect : subEffects) {
            subEffect.start(game, target);
        }
    }

    @Override
    public void end(Game game, MutableStatHolder target) {
        for (Effect subEffect : subEffects) {
            subEffect.end(game, target);
        }
    }

    @Override
    public void eachRound(Game game, MutableStatHolder target) {
        for (Effect subEffect : subEffects) {
            subEffect.eachRound(game, target);
        }
    }
}
