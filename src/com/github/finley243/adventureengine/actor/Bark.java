package com.github.finley243.adventureengine.actor;

import com.github.finley243.adventureengine.MathUtils;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.NounMapper;

import java.util.List;

public class Bark {

    private final SensoryEvent.ResponseType responseType;
    private final List<String> visiblePhrases;
    private final List<String> nonVisiblePhrases;

    public Bark(SensoryEvent.ResponseType responseType, List<String> visiblePhrases, List<String> nonVisiblePhrases) {
        this.responseType = responseType;
        this.visiblePhrases = visiblePhrases;
        this.nonVisiblePhrases = nonVisiblePhrases;
    }

    public void trigger(Actor subject, Actor target) {
        String visiblePhrase = MathUtils.selectRandomFromList(visiblePhrases);
        String nonVisiblePhrase = MathUtils.selectRandomFromList(nonVisiblePhrases);
        Context context = new Context(new NounMapper().put("actor", subject).build());
        subject.game().eventBus().post(new SensoryEvent(subject.getArea(), visiblePhrase, nonVisiblePhrase, context, responseType, false, null, subject, target));
    }

}
