package com.github.finley243.adventureengine.actor.ai.behavior;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionSleep;
import com.github.finley243.adventureengine.action.ActionUseStart;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.world.object.ObjectBed;

public class BehaviorSleep extends Behavior {

    private final String bed;

    public BehaviorSleep(Condition condition, String bed) {
        super(condition);
        this.bed = bed;
    }

    @Override
    public boolean actionIsTarget(Action action) {
        if(action instanceof ActionUseStart) {
            return ((ActionUseStart) action).getObject() instanceof ObjectBed && ((ActionUseStart) action).getObject().getID().equals(bed);
        } else if(action instanceof ActionSleep) {
            return true;
        }
        return false;
    }

}
