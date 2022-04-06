package com.github.finley243.adventureengine.effect;

import com.github.finley243.adventureengine.actor.Actor;

public class EffectTrigger extends Effect {

    private final String trigger;

    public EffectTrigger(int duration, boolean manualRemoval, String trigger) {
        super(duration, manualRemoval);
        this.trigger = trigger;
    }

    @Override
    public void start(Actor target) {
        target.triggerSpecial(trigger);
    }

    @Override
    public void end(Actor target) {

    }

    @Override
    public void eachTurn(Actor target) {
        target.triggerSpecial(trigger);
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o) && trigger.equals(((EffectTrigger) o).trigger);
    }

    @Override
    public int hashCode() {
        return (31 * super.hashCode()) + trigger.hashCode();
    }

}
