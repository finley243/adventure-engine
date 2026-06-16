package com.github.finley243.adventureengine.actor.ai;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.event.SensoryEventDispatcher;

public class Idle {

    private final SensoryEventDispatcher sensoryEventDispatcher;
    private final Condition condition;
    private final String phrase;

    public Idle(SensoryEventDispatcher sensoryEventDispatcher, Condition condition, String phrase) {
        if (phrase == null) throw new IllegalArgumentException("Idle phrase cannot be null");
        this.sensoryEventDispatcher = sensoryEventDispatcher;
        this.condition = condition;
        this.phrase = phrase;
    }

    public Condition getCondition() {
        return condition;
    }

    public boolean canPlay(Actor subject) {
        return condition == null || condition.isMet(Context.builder().subject(subject).build());
    }

    public void trigger(Actor subject) {
        Context context = Context.builder().subject(subject).build();
        sensoryEventDispatcher.dispatch(new SensoryEvent(subject.getArea(), phrase, context, true, null, null));
    }

}
