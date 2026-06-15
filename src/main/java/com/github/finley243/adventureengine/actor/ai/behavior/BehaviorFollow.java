package com.github.finley243.adventureengine.actor.ai.behavior;

import com.github.finley243.adventureengine.GameDataException;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ai.Idle;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.gamedata.Registry;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.object.WorldObject;

import java.util.List;

public class BehaviorFollow extends Behavior {

    private String actorID;
    private Actor actor;

    public BehaviorFollow(Condition condition, Script startScript, Script eachRoundScript, int duration, List<Idle> idles, String actorID) {
        super(condition, startScript, eachRoundScript, duration, idles);
        this.actorID = actorID;
    }

    @Override
    public void resolveReferences(Registry<Area> areaRegistry, Registry<WorldObject> objectRegistry, Registry<Actor> actorRegistry) {
        if (this.actor != null) throw new GameDataException("BehaviorFollow actor has already been resolved");
        Actor actor = actorRegistry.getFromID(actorID);
        if (actor == null) throw new GameDataException("BehaviorFollow has invalid actor reference");
        this.actor = actor;
        this.actorID = null;
    }

    @Override
    public boolean isInTargetState(Actor subject) {
        return getActor().getArea().equals(subject.getArea());
    }

    @Override
    public Area getTargetArea(Actor subject) {
        return getActor().getArea();
    }

    private Actor getActor() {
        if (actor == null) throw new GameDataException("BehaviorFollow actor has not been resolved");
        return actor;
    }

}
