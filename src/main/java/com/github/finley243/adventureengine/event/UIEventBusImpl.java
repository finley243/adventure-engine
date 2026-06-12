package com.github.finley243.adventureengine.event;

import com.github.finley243.adventureengine.event.ui.UIEvent;
import com.google.common.eventbus.EventBus;

public class UIEventBusImpl implements UIEventBus {

    private final EventBus eventBus;

    public UIEventBusImpl() {
        this.eventBus = new EventBus();
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
        eventBus.post(event);
    }

}
