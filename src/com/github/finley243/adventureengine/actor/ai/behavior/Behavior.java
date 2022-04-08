package com.github.finley243.adventureengine.actor.ai.behavior;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ai.BehaviorTarget;
import com.github.finley243.adventureengine.condition.Condition;

import java.util.List;

public class Behavior {

    public enum BehaviorType {
        PATROL, SLEEP, OBJECT, FOLLOW
    }

    private final BehaviorType type;
    private final Condition condition;
    // empty = wander within room, 1 = stationary, >1 = patrol path (uses the shortest path between points)
    private final List<BehaviorTarget> targets;

    public Behavior(BehaviorType type, Condition condition, List<BehaviorTarget> targets) {
        this.type = type;
        this.condition = condition;
        this.targets = targets;
    }

    public boolean shouldTrigger(Actor subject) {
        return condition == null || condition.isMet(subject);
    }

    public BehaviorType getType() {
        return type;
    }

    public List<BehaviorTarget> getTargets() {
        return targets;
    }

}
