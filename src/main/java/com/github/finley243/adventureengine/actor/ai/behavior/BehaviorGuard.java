package com.github.finley243.adventureengine.actor.ai.behavior;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.GameDataException;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ai.Idle;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.gamedata.Registry;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.object.WorldObject;
import com.google.errorprone.annotations.OverridingMethodsMustInvokeSuper;

import java.util.List;

public class BehaviorGuard extends Behavior {

    private String guardTargetID;
    private WorldObject guardTargetObject;

    public BehaviorGuard(Condition condition, Script startScript, Script eachRoundScript, int duration, List<Idle> idles, String guardTargetID) {
        super(condition, startScript, eachRoundScript, duration, idles);
        this.guardTargetID = guardTargetID;
    }

    @Override
    public void resolveReferences(Registry<Area> areaRegistry, Registry<WorldObject> objectRegistry, Registry<Actor> actorRegistry) {
        if (this.guardTargetObject != null) throw new GameDataException("BehaviorGuard object has already been resolved");
        WorldObject object = objectRegistry.getFromID(guardTargetID);
        if (object == null) throw new GameDataException("BehaviorGuard has invalid object reference");
        this.guardTargetObject = object;
        this.guardTargetID = null;
    }

    @Override
    public void update(Actor subject, Context scriptContext) {
        super.update(subject, scriptContext);
        updateObjectState(subject);
    }

    @Override
    public boolean isInTargetState(Actor subject) {
        return !subject.isInCombat() && getGuardTargetObject().getArea().equals(subject.getArea());
    }

    @Override
    public Area getTargetArea(Actor subject) {
        return getGuardTargetObject().getArea();
    }

    @Override
    public void onPerformAction(Actor subject, Action action) {
        super.onPerformAction(subject, action);
        updateObjectState(subject);
    }

    private WorldObject getGuardTargetObject() {
        if (guardTargetObject == null) throw new GameDataException("BehaviorGuard object has not been resolved");
        return guardTargetObject;
    }

    private void updateObjectState(Actor subject) {
        if (isInTargetState(subject)) {
            guardTargetObject.addGuard(subject);
        } else {
            guardTargetObject.removeGuard(subject);
        }
    }

}
