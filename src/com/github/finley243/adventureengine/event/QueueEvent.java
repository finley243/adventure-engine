package com.github.finley243.adventureengine.event;

public class QueueEvent {

    private final Object event;

    public QueueEvent(Object event) {
        this.event = event;
    }

    public Object getEvent() {
        return event;
    }

}
