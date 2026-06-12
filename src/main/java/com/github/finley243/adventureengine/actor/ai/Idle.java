package com.github.finley243.adventureengine.actor.ai;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.script.ScriptRuntime;

public class Idle {

    private final Condition condition;
    private final String phrase;

    public Idle(Condition condition, String phrase) {
        if (phrase == null) throw new IllegalArgumentException("Idle phrase cannot be null");
        this.condition = condition;
        this.phrase = phrase;
    }

    public boolean canPlay(Actor subject, ScriptRuntime scriptRuntime) {
        return condition == null || condition.isMet(scriptRuntime, Context.builder().subject(subject).target(subject).build());
    }

    public void trigger(Actor subject) {
        Context context = Context.builder().subject(subject).build();
        SensoryEvent.execute(game, new SensoryEvent(subject.getArea(), phrase, context, true, null, null));
    }

}
