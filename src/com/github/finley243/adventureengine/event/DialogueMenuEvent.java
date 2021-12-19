package com.github.finley243.adventureengine.event;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;

import java.util.List;

public class DialogueMenuEvent {

    private final String startTopic;
    private final Actor actor;

    public DialogueMenuEvent(String startTopic, Actor actor) {
        this.startTopic = startTopic;
        this.actor = actor;
    }

    public String getStartTopic() {
        return startTopic;
    }

    public Actor getActor() {
        return actor;
    }

}
