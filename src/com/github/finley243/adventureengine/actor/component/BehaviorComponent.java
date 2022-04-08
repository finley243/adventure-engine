package com.github.finley243.adventureengine.actor.component;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ai.behavior.Behavior;

import java.util.List;

public class BehaviorComponent {

    private final Actor actor;
    private final List<Behavior> behaviors;
    private int currentBehavior;
    private int currentTarget;

    public BehaviorComponent(Actor actor, List<Behavior> behaviors) {
        this.actor = actor;
        this.behaviors = behaviors;
    }

    public Behavior currentBehavior() {
        if(behaviors.isEmpty()) return null;
        return behaviors.get(currentBehavior);
    }

    public void update() {

    }

}
