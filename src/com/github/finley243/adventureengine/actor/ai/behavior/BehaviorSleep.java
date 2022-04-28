package com.github.finley243.adventureengine.actor.ai.behavior;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionSleep;
import com.github.finley243.adventureengine.action.ActionUseStart;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.scene.Scene;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.object.ObjectBed;

import java.util.List;

public class BehaviorSleep extends Behavior {

    private final String bed;

    public BehaviorSleep(Condition condition, List<Scene> idleScenes, String bed) {
        super(condition, 0, idleScenes);
        this.bed = bed;
    }

    @Override
    public boolean isInTargetState(Actor subject) {
        return subject.isSleeping();
    }

    @Override
    public Area getTargetArea(Actor subject) {
        return subject.game().data().getObject(bed).getArea();
    }

    @Override
    public float actionUtilityOverride(Action action) {
        if(action instanceof ActionUseStart && ((ActionUseStart) action).getObject() instanceof ObjectBed && ((ActionUseStart) action).getObject().getID().equals(bed)) {
            System.out.println("Check valid action - useObject");
            return BEHAVIOR_ACTION_UTILITY;
        } else if(action instanceof ActionSleep) {
            System.out.println("Check valid action - sleep");
            return BEHAVIOR_ACTION_UTILITY;
        }
        return -1.0f;
    }

}
