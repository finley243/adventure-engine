package com.github.finley243.adventureengine;

import com.github.finley243.adventureengine.event.QueuedEvent;
import com.github.finley243.adventureengine.event.SceneChoiceMenuEvent;
import com.github.finley243.adventureengine.event.SceneLineEvent;
import com.github.finley243.adventureengine.scene.Scene;

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
        //System.out.println("Execute: " + currentEvent);
        currentEvent.execute(game);
    }

    // Removes all SceneLineEvent/SceneChoiceEvent from the given scene until reaching a non-matching event
    // Intended for use when a line causes an exit or redirect
    public void removeQueuedScene(Scene scene) {
        while (queue.peekFirst() instanceof SceneLineEvent sceneLineEvent && sceneLineEvent.getScene().equals(scene) ||
                queue.peekFirst() instanceof SceneChoiceMenuEvent sceneChoiceMenuEvent && sceneChoiceMenuEvent.getScene().equals(scene)) {
            queue.removeFirst();
        }
    }

    public void addToFront(QueuedEvent event) {
        //System.out.println("Add to queue (FRONT): " + event);
        queue.addFirst(event);
    }

    public void addAllToFront(List<QueuedEvent> events) {
        //System.out.println("Add to queue (MULTIPLE, FRONT):");
        for (int i = events.size() - 1; i >= 0; i--) {
            //System.out.println(" - " + events.get(i));
            queue.addFirst(events.get(i));
        }
    }

    public void addToEnd(QueuedEvent event) {
        //System.out.println("Add to queue: " + event);
        queue.addLast(event);
    }

    public void addAllToEnd(List<QueuedEvent> events) {
        for (QueuedEvent event : events) {
            queue.addLast(event);
        }
    }

}
