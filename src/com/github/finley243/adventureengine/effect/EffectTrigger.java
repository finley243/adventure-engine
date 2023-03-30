package com.github.finley243.adventureengine.effect;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.stat.EffectableStatHolder;

public class EffectTrigger extends Effect {

    private final String trigger;

    public EffectTrigger(Game game, String ID, int duration, boolean manualRemoval, boolean stackable, Condition conditionAdd, Condition conditionRemove, Condition conditionActive, Script scriptAdd, Script scriptRemove, Script scriptRound, String trigger) {
        super(game, ID, duration, manualRemoval, stackable, conditionAdd, conditionRemove, conditionActive, scriptAdd, scriptRemove, scriptRound);
        this.trigger = trigger;
    }

    @Override
    public void start(EffectableStatHolder target) {
        target.triggerEffect(trigger);
    }

    @Override
    public void eachRound(EffectableStatHolder target) {
        target.triggerEffect(trigger);
    }

}
