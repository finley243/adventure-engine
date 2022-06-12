package com.github.finley243.adventureengine.actor.ai.behavior;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.object.WorldObject;

import java.util.List;

public class BehaviorCycle extends Behavior {

    private final List<Behavior> stages;
    private int currentStage;

    public BehaviorCycle(Condition condition, List<Behavior> stages) {
        super(condition, 0, null);
        if(stages.isEmpty()) throw new IllegalArgumentException("BehaviorCycle stages cannot be empty");
        if(stages.size() == 1) throw new IllegalArgumentException("BehaviorCycle cannot have 1 stage");
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
        stages.get(currentStage).update(subject);
        if(stages.get(currentStage).hasCompleted(subject)) {
            currentStage += 1;
            if(currentStage >= stages.size()) {
                currentStage = 0;
            }
            stages.get(currentStage).onStart();
        }
    }

    @Override
    public void onStart() {
        currentStage = 0;
        stages.get(currentStage).onStart();
    }

    @Override
    public Area getTargetArea(Actor subject) {
        return stages.get(currentStage).getTargetArea(subject);
    }

    @Override
    public float actionUtilityOverride(Actor subject, Action action) {
        return stages.get(currentStage).actionUtilityOverride(subject, action);
    }

    @Override
    public boolean isGuarding(Actor subject, WorldObject object) {
        return stages.get(currentStage).isGuarding(subject, object);
    }

}
