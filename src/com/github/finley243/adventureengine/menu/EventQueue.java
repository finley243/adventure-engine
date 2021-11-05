package com.github.finley243.adventureengine.menu;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.event.NextQueueEvent;
import com.github.finley243.adventureengine.event.QueueEvent;
import com.google.common.eventbus.Subscribe;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;

public class EventQueue {

    // Do not queue GUI events, only game events
    private final Deque<Object> events;

    public EventQueue() {
        this.events = new LinkedList<>();
    }

    @Subscribe
    public void nextEvent(NextQueueEvent e) {
        if(!events.isEmpty()) {
            System.out.println("Dequeue event: " + events.peek().getClass().getName());
            Game.EVENT_BUS.post(events.remove());
        }
    }

    @Subscribe
    public void onQueueEvent(QueueEvent e) {
        System.out.println("Queue event: " + e.getEvent().getClass().getName());
        events.add(e.getEvent());
    }

}
