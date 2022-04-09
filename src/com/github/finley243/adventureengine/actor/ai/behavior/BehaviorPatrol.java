package com.github.finley243.adventureengine.actor.ai.behavior;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.condition.Condition;

public class BehaviorPatrol extends Behavior {

    public BehaviorPatrol(Condition condition) {
        super(condition);
    }

    @Override
    public boolean actionIsTarget(Action action) {
        return false;
    }

}
