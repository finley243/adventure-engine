package com.github.finley243.adventureengine;

import com.github.finley243.adventureengine.event.QueuedEvent;

import java.util.*;

public class EventQueue {

    private final Game game;
    private final Deque<QueuedEvent> queue;

    public EventQueue(Game game) {
        this.game = game;
        this.queue = new ArrayDeque<>();
    }

    public void executeNext() {
        if (queue.isEmpty()) return;
        QueuedEvent currentEvent = queue.removeFirst();
        currentEvent.execute(game);
    }

    public void addToFront(QueuedEvent event) {
        queue.addFirst(event);
    }

    public void addAllToFront(List<QueuedEvent> events) {
        for (int i = events.size() - 1; i >= 0; i--) {
            queue.addFirst(events.get(i));
        }
    }

    public void addToEnd(QueuedEvent event) {
        queue.addLast(event);
    }

    public void addAllToEnd(List<QueuedEvent> events) {
        for (QueuedEvent event : events) {
            queue.addLast(event);
        }
    }

}
