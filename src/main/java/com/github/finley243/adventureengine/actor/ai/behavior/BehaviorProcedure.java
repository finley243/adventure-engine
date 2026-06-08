package com.github.finley243.adventureengine.actor.ai.behavior;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ai.Idle;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.object.WorldObject;

import java.util.List;

public class BehaviorProcedure extends Behavior {

    private final boolean isCycle;
    private final List<Behavior> stages;
    private int currentStage;
    private Context currentStageContext;

    public BehaviorProcedure(Condition condition, Script startScript, Script eachRoundScript, boolean isCycle, List<Behavior> stages) {
        super(condition, eachRoundScript, startScript, 0, null);
        if (stages.isEmpty()) throw new IllegalArgumentException("BehaviorCycle stages cannot be empty");
        if (stages.size() == 1) throw new IllegalArgumentException("BehaviorCycle cannot have 1 stage");
        for (Behavior behavior : stages) {
            if (behavior instanceof BehaviorProcedure) throw new IllegalArgumentException("BehaviorProcedure cannot contain another BehaviorProcedure");
        }
        this.isCycle = isCycle;
        this.stages = stages;
    }

    @Override
    public boolean isInTargetState(Game game, Actor subject) {
        return stages.get(currentStage).isInTargetState(game, subject);
    }

    @Override
    public void onPerformAction(Game game, Actor subject, Action action) {
        stages.get(currentStage).onPerformAction(game, subject, action);
    }

    @Override
    public boolean hasCompleted(Game game, Actor subject) {
        if (isCycle) {
            return false;
        } else {
            return currentStage == stages.size() - 1 && isInTargetState(game, subject);
        }
    }

    @Override
    public void updateTurn(Game game, Actor subject, Context scriptContext) {
        triggerRoundScript(scriptContext);
        stages.get(currentStage).updateTurn(game, subject, currentStageContext);
    }

    @Override
    public void update(Game game, Actor subject, Context scriptContext) {
        stages.get(currentStage).update(game, subject, currentStageContext);
        if (stages.get(currentStage).hasCompleted(game, subject)) {
            currentStage += 1;
            if (currentStage >= stages.size()) {
                currentStage = 0;
            }
            resetStageContext(scriptContext);
            stages.get(currentStage).onStart(currentStageContext);
        }
    }

    @Override
    public void onStart(Context scriptContext) {
        currentStage = 0;
        resetStageContext(scriptContext);
        stages.get(currentStage).onStart(currentStageContext);
    }

    @Override
    public Area getTargetArea(Game game, Actor subject) {
        return stages.get(currentStage).getTargetArea(game, subject);
    }

    @Override
    public Float actionUtilityOverride(Game game, Actor subject, Action action) {
        return stages.get(currentStage).actionUtilityOverride(game, subject, action);
    }

    @Override
    public boolean isGuarding(Actor subject, WorldObject object) {
        return stages.get(currentStage).isGuarding(subject, object);
    }

    @Override
    public Idle getIdle(Game game, Actor subject) {
        return stages.get(currentStage).getIdle(game, subject);
    }

    private void resetStageContext(Context parentContext) {
        currentStageContext = Context.from(parentContext).build();
    }

}
