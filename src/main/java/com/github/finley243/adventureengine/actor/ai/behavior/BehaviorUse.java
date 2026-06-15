package com.github.finley243.adventureengine.actor.ai.behavior;

import com.github.finley243.adventureengine.GameDataException;
import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionObjectUseEnd;
import com.github.finley243.adventureengine.action.ActionObjectUseStart;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ai.Idle;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.gamedata.Registry;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.object.WorldObject;

import java.util.List;

public class BehaviorUse extends Behavior {

    private String objectID;
    private WorldObject object;
    private final String slot;

    public BehaviorUse(Condition condition, Script startScript, Script eachRoundScript, int duration, List<Idle> idles, String objectID, String slot) {
        super(condition, startScript, eachRoundScript, duration, idles);
        this.objectID = objectID;
        this.slot = slot;
    }

    @Override
    public void resolveReferences(Registry<Area> areaRegistry, Registry<WorldObject> objectRegistry, Registry<Actor> actorRegistry) {
        if (this.object != null) throw new GameDataException("BehaviorUse object has already been resolved");
        WorldObject object = objectRegistry.getFromID(objectID);
        if (object == null) throw new GameDataException("BehaviorUse has invalid object reference");
        this.object = object;
        this.objectID = null;
    }

    @Override
    public boolean isInTargetState(Actor subject) {
        return subject.isUsingObject() && subject.getUsingObject().object().equals(getObject()) && subject.getUsingObject().slot().equals(slot);
    }

    @Override
    public Area getTargetArea(Actor subject) {
        return getObject().getArea();
    }

    @Override
    public Float actionUtilityOverride(Actor subject, Action action) {
        if (action instanceof ActionObjectUseStart actionUseStart && actionUseStart.getComponent().getObject().equals(getObject()) && actionUseStart.getSlotID().equals(slot)) {
            return subject.isInCombat() ? BEHAVIOR_ACTION_UTILITY_COMBAT : BEHAVIOR_ACTION_UTILITY;
        } else if (action instanceof ActionObjectUseEnd actionUseEnd && actionUseEnd.getComponent().getObject().equals(getObject()) && actionUseEnd.getSlotID().equals(slot)) {
            return 0.0f;
        }
        return super.actionUtilityOverride(subject, action);
    }

    private WorldObject getObject() {
        if (object == null) throw new GameDataException("BehaviorUse object has not been resolved");
        return object;
    }

}
