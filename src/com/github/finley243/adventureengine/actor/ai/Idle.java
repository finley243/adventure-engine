package com.github.finley243.adventureengine.actor.ai;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.textgen.TextContext;
import com.github.finley243.adventureengine.MapBuilder;
import com.github.finley243.adventureengine.textgen.Noun;

public class Idle {

    private final Condition condition;
    private final String phrase;

    public Idle(Condition condition, String phrase) {
        if (phrase == null) throw new IllegalArgumentException("Idle phrase cannot be null");
        this.condition = condition;
        this.phrase = phrase;
    }

    public boolean canPlay(Actor subject) {
        return condition == null || condition.isMet(new Context(subject.game(), subject, subject));
    }

    public void trigger(Actor subject) {
        TextContext context = new TextContext(new MapBuilder<String, Noun>().put("actor", subject).build());
        subject.game().eventQueue().addToEnd(new SensoryEvent(subject.getArea(), phrase, context, true, null, null, subject, null));
    }

}
