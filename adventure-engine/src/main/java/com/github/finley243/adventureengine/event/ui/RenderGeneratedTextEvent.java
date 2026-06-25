package com.github.finley243.adventureengine.event.ui;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.textgen.TextContext;

public class RenderGeneratedTextEvent extends UIEvent {

    private final String text;
    private final Context context;
    private final TextContext textContext;

    public RenderGeneratedTextEvent(String text, Context context, TextContext textContext) {
        this.text = text;
        this.context = context;
        this.textContext = textContext;
    }

    public String getText() {
        return text;
    }

    public Context getContext() {
        return context;
    }

    public TextContext getTextContext() {
        return textContext;
    }

}
