package com.github.finley243.adventureengine.event;

import com.github.finley243.adventureengine.actor.Actor;

public class DialogueMenuEvent {

    private Actor subject;
    private String startTopic;

    public DialogueMenuEvent(Actor subject, String startTopic) {
        this.subject = subject;
        this.startTopic = startTopic;
    }

    public Actor getSubject() {
        return subject;
    }

    public String getStartTopic() {
        return startTopic;
    }

}
