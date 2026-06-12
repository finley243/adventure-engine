package com.github.finley243.adventureengine.event;

import com.github.finley243.adventureengine.event.ui.UIEvent;

public interface UIEventBus {

    void register(Object listener);

    void unregister(Object listener);

    void post(UIEvent event);

}
