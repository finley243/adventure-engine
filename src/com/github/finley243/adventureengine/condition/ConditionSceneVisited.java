package com.github.finley243.adventureengine.condition;

import com.github.finley243.adventureengine.actor.Actor;

public class ConditionSceneVisited extends Condition {

    private final String topicID;

    public ConditionSceneVisited(boolean invert, String topicID) {
        super(invert);
        this.topicID = topicID;
    }

    @Override
    public boolean isMetInternal(Actor subject, Actor target) {
        return subject.game().data().getScene(topicID).hasVisited();
    }

}
