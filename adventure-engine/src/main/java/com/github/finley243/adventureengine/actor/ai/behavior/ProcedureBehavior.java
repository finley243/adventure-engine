package com.github.finley243.adventureengine.actor.ai.behavior;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ai.Idle;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.gamedata.Registry;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.script.ScriptRuntime;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.object.WorldObject;

import java.util.List;

public class ProcedureBehavior extends Behavior {

    private final boolean isCycle;
    private final List<Behavior> stages;
    private int currentStage;
    private Context currentStageContext;

    public ProcedureBehavior(Condition condition, Script startScript, Script eachRoundScript, boolean isCycle, List<Behavior> stages) {
        super(condition, eachRoundScript, startScript, 0, null);
        if (stages.isEmpty()) throw new IllegalArgumentException("BehaviorCycle stages cannot be empty");
        if (stages.size() == 1) throw new IllegalArgumentException("BehaviorCycle cannot have 1 stage");
        for (Behavior behavior : stages) {
            if (behavior instanceof ProcedureBehavior) throw new IllegalArgumentException("BehaviorProcedure cannot contain another BehaviorProcedure");
        }
        this.isCycle = isCycle;
        this.stages = stages;
    }

    @Override
    public void resolveReferences(Registry<Area> areaRegistry, Registry<WorldObject> objectRegistry, Registry<Actor> actorRegistry) {
        for (Behavior behavior : stages) {
            behavior.resolveReferences(areaRegistry, objectRegistry, actorRegistry);
        }
    }

    @Override
    public boolean isInTargetState(Actor subject) {
        return stages.get(currentStage).isInTargetState(subject);
    }

    @Override
    public void onPerformAction(Actor subject, Action action) {
        stages.get(currentStage).onPerformAction(subject, action);
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
    public void updateTurn(Actor subject, ScriptRuntime scriptRuntime) {
        triggerRoundScript(subject, scriptRuntime);
        stages.get(currentStage).updateTurn(subject, scriptRuntime);
    }

    @Override
    public void update(Actor subject, ScriptRuntime scriptRuntime, Context scriptContext) {
        super.update(subject, scriptRuntime, scriptContext);
        stages.get(currentStage).update(subject, scriptRuntime, currentStageContext);
        if (stages.get(currentStage).hasCompleted(subject)) {
            currentStage += 1;
            if (currentStage >= stages.size()) {
                currentStage = 0;
            }
            resetStageContext(scriptContext);
            stages.get(currentStage).onStart(subject, scriptRuntime, scriptContext);
        }
    }

    @Override
    public void onStart(Actor subject, ScriptRuntime scriptRuntime, Context scriptContext) {
        currentStage = 0;
        resetStageContext(scriptContext);
        stages.get(currentStage).onStart(subject, scriptRuntime, scriptContext);
    }

    @Override
    public Area getTargetArea(Actor subject) {
        return stages.get(currentStage).getTargetArea(subject);
    }

    @Override
    public Float actionUtilityOverride(Actor subject, Action action) {
        return stages.get(currentStage).actionUtilityOverride(subject, action);
    }

    @Override
    public Idle getIdle(Actor subject) {
        return stages.get(currentStage).getIdle(subject);
    }

    private void resetStageContext(Context parentContext) {
        currentStageContext = Context.from(parentContext).build();
    }

}
