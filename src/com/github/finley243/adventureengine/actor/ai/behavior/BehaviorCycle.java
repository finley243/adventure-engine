package com.github.finley243.adventureengine.actor.ai.behavior;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.world.environment.Area;

import java.util.ArrayList;
import java.util.List;

public class BehaviorCycle extends Behavior {

    private final List<Behavior> stages;
    private int currentStage;

    public BehaviorCycle(Condition startCondition, Condition endCondition, List<Behavior> stages) {
        super(startCondition, endCondition, 0, false, null);
        if(stages.isEmpty()) throw new IllegalArgumentException("BehaviorCycle stages cannot be empty");
        for(Behavior behavior : stages) {
            if(behavior instanceof BehaviorCycle) throw new IllegalArgumentException("BehaviorCycle cannot contain another BehaviorCycle");
        }
        this.stages = stages;
    }

    @Override
    public boolean isInTargetState(Actor subject) {
        return stages.get(currentStage).isInTargetState(subject);
    }

    @Override
    public void update(Actor subject) {
        super.update(subject);
        if(stages.get(currentStage).shouldEnd(subject)) {
            currentStage += 1;
            if(currentStage >= stages.size()) {
                currentStage = 0;
            }
        }
    }

    @Override
    public Area getTargetArea(Actor subject) {
        return stages.get(currentStage).getTargetArea(subject);
    }

    @Override
    public float actionUtilityOverride(Action action) {
        return stages.get(currentStage).actionUtilityOverride(action);
    }

}
