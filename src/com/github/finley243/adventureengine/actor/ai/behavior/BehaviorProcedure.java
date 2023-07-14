package com.github.finley243.adventureengine.actor.ai.behavior;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.object.WorldObject;

import java.util.List;

public class BehaviorProcedure extends Behavior {

    private final boolean isCycle;
    private final List<Behavior> stages;
    private int currentStage;

    public BehaviorProcedure(Condition condition, Script eachRoundScript, boolean isCycle, List<Behavior> stages) {
        super(condition, eachRoundScript, 0, null);
        if (stages.isEmpty()) throw new IllegalArgumentException("BehaviorCycle stages cannot be empty");
        if (stages.size() == 1) throw new IllegalArgumentException("BehaviorCycle cannot have 1 stage");
        for (Behavior behavior : stages) {
            if (behavior instanceof BehaviorProcedure) throw new IllegalArgumentException("BehaviorProcedure cannot contain another BehaviorProcedure");
        }
        this.isCycle = isCycle;
        this.stages = stages;
    }

    @Override
    public boolean isInTargetState(Actor subject) {
        return stages.get(currentStage).isInTargetState(subject);
    }

    @Override
    public boolean hasCompleted(Actor subject) {
        if (isCycle) {
            return false;
        } else {
            return currentStage == stages.size() - 1 && isInTargetState(subject);
        }
    }

    @Override
    public void updateTurn(Actor subject) {
        triggerRoundScript(subject);
        stages.get(currentStage).updateTurn(subject);
    }

    @Override
    public void update(Actor subject) {
        stages.get(currentStage).update(subject);
        if (stages.get(currentStage).hasCompleted(subject)) {
            currentStage += 1;
            if (currentStage >= stages.size()) {
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
