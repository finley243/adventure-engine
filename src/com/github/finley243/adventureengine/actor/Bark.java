package com.github.finley243.adventureengine.actor;

import com.github.finley243.adventureengine.Context;
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

    public void trigger(Context context) {
        String visiblePhrase = MathUtils.selectRandomFromList(visiblePhrases);
        String nonVisiblePhrase = MathUtils.selectRandomFromList(nonVisiblePhrases);
        TextContext textContext = new TextContext(context.getTextVarMap(), context.getContextNounMap());
        context.game().eventQueue().addToEnd(new SensoryEvent(context.getSubject().getArea(), visiblePhrase, nonVisiblePhrase, textContext, false, null, this, context.getSubject(), context.getTarget()));
    }

    public BarkResponseType responseType() {
        return responseType;
    }

}
