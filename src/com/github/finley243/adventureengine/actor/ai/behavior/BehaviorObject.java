package com.github.finley243.adventureengine.actor.ai.behavior;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionUseStart;
import com.github.finley243.adventureengine.condition.Condition;

public class BehaviorObject extends Behavior {

    private final String objectID;

    public BehaviorObject(Condition condition, String objectID) {
        super(condition);
        this.objectID = objectID;
    }

    @Override
    public boolean actionIsTarget(Action action) {
        if(action instanceof ActionUseStart) {
            return ((ActionUseStart) action).getObject().getID().equals(objectID);
        }
        return false;
    }

}
