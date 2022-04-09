package com.github.finley243.adventureengine.actor.ai.behavior;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionUseStart;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.world.object.ObjectBed;

public class BehaviorSandbox extends Behavior {

    private final String room;

    public BehaviorSandbox(Condition condition, String room) {
        super(condition);
        this.room = room;
    }

    @Override
    public boolean actionIsTarget(Action action) {
        if(action instanceof ActionUseStart) {
            return !(((ActionUseStart) action).getObject() instanceof ObjectBed);
        }
        return false;
    }
}
