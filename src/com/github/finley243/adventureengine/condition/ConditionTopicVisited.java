package com.github.finley243.adventureengine.condition;

import com.github.finley243.adventureengine.actor.Actor;

public class ConditionTopicVisited extends Condition {

    private final String topicID;

    public ConditionTopicVisited(boolean invert, String topicID) {
        super(invert);
        this.topicID = topicID;
    }

    @Override
    public boolean isMet(Actor subject) {
        return subject.game().data().getTopic(topicID).hasVisited() != invert;
    }

}
