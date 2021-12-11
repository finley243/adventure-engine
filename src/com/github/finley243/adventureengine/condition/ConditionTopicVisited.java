package com.github.finley243.adventureengine.condition;

import com.github.finley243.adventureengine.Data;
import com.github.finley243.adventureengine.actor.Actor;

public class ConditionTopicVisited implements Condition {

    private final String topicID;

    public ConditionTopicVisited(String topicID) {
        this.topicID = topicID;
    }

    @Override
    public boolean isMet(Actor subject) {
        return Data.getTopic(topicID).hasVisited();
    }

    @Override
    public String getChoiceTag() {
        return null;
    }

}
