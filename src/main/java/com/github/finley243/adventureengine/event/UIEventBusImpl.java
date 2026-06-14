package com.github.finley243.adventureengine.event;

import com.github.finley243.adventureengine.event.ui.RenderGeneratedTextEvent;
import com.github.finley243.adventureengine.event.ui.RenderTextEvent;
import com.github.finley243.adventureengine.event.ui.UIEvent;
import com.github.finley243.adventureengine.textgen.TextGen;
import com.google.common.eventbus.EventBus;

public class UIEventBusImpl implements UIEventBus {

    private final EventBus eventBus;
    private final TextGen textGen;

    public UIEventBusImpl(TextGen textGen) {
        this.eventBus = new EventBus();
        this.textGen = textGen;
    }

    @Override
    public void register(Object listener) {
        eventBus.register(listener);
    }

    @Override
    public void unregister(Object listener) {
        eventBus.unregister(listener);
    }

    @Override
    public void post(UIEvent event) {
        if (event instanceof RenderGeneratedTextEvent generatedTextEvent) {
            String generatedText = textGen.generate(generatedTextEvent.getText(), generatedTextEvent.getContext(), generatedTextEvent.getTextContext());
            event = new RenderTextEvent(generatedText);
        }
        eventBus.post(event);
    }

}
