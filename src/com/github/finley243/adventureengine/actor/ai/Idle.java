package com.github.finley243.adventureengine.actor.ai;

import com.github.finley243.adventureengine.ContextScript;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.NounMapper;

public class Idle {

    private final Condition condition;
    private final String phrase;

    public Idle(Condition condition, String phrase) {
        if (phrase == null) throw new IllegalArgumentException("Idle phrase cannot be null");
        this.condition = condition;
        this.phrase = phrase;
    }

    public boolean canPlay(Actor subject) {
        return condition == null || condition.isMet(new ContextScript(subject.game(), subject, subject, null));
    }

    public void trigger(Actor subject) {
        Context context = new Context(new NounMapper().put("actor", subject).build());
        subject.game().eventBus().post(new SensoryEvent(subject.getArea(), phrase, context, null, null, subject, null));
    }

}
