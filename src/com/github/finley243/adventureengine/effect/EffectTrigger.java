package com.github.finley243.adventureengine.effect;

import com.github.finley243.adventureengine.effect.moddable.Moddable;

public class EffectTrigger extends Effect {

    private final String trigger;

    public EffectTrigger(int duration, boolean manualRemoval, boolean stackable, String trigger) {
        super(duration, manualRemoval, stackable);
        this.trigger = trigger;
    }

    @Override
    public void start(Moddable target) {
        target.triggerEffect(trigger);
    }

    @Override
    public void end(Moddable target) {

    }

    @Override
    public void eachTurn(Moddable target) {
        target.triggerEffect(trigger);
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
