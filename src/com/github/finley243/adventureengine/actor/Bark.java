package com.github.finley243.adventureengine.actor;

import com.github.finley243.adventureengine.MathUtils;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.textgen.TextContext;
import com.github.finley243.adventureengine.MapBuilder;
import com.github.finley243.adventureengine.textgen.Noun;

import java.util.List;

public class Bark {

    public enum BarkResponseType {
        NONE, HOSTILE
    }

    private final BarkResponseType responseType;
    private final List<String> visiblePhrases;
    private final List<String> nonVisiblePhrases;

    public Bark(BarkResponseType responseType, List<String> visiblePhrases, List<String> nonVisiblePhrases) {
        this.responseType = responseType;
        this.visiblePhrases = visiblePhrases;
        this.nonVisiblePhrases = nonVisiblePhrases;
    }

    public void trigger(Actor subject, Actor target) {
        String visiblePhrase = MathUtils.selectRandomFromList(visiblePhrases);
        String nonVisiblePhrase = MathUtils.selectRandomFromList(nonVisiblePhrases);
        TextContext context = new TextContext(new MapBuilder<String, Noun>().put("actor", subject).build());
        subject.game().eventBus().post(new SensoryEvent(subject.getArea(), visiblePhrase, nonVisiblePhrase, context, false, null, this, subject, target));
    }

    public BarkResponseType responseType() {
        return responseType;
    }

}
