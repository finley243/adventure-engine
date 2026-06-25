package com.github.finley243.adventureengine.actor.ai.behavior;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ai.Idle;
import com.github.finley243.adventureengine.condition.Condition;
import com.github.finley243.adventureengine.gamedata.Registry;
import com.github.finley243.adventureengine.load.GameDataException;
import com.github.finley243.adventureengine.script.Script;
import com.github.finley243.adventureengine.world.environment.Area;
import com.github.finley243.adventureengine.world.object.WorldObject;

import java.util.List;

public class MoveBehavior extends Behavior {

    private String areaID;
    private Area area;

    public MoveBehavior(Condition condition, Script startScript, Script eachRoundScript, int duration, List<Idle> idles, String areaID) {
        super(condition, startScript, eachRoundScript, duration, idles);
        this.areaID = areaID;
    }

    @Override
    public void resolveReferences(Registry<Area> areaRegistry, Registry<WorldObject> objectRegistry, Registry<Actor> actorRegistry) {
        if (this.area != null) throw new GameDataException("BehaviorMove area has already been resolved");
        Area area = areaRegistry.getFromID(areaID);
        if (area == null) throw new GameDataException("BehaviorMove has invalid area reference");
        this.area = area;
        this.areaID = null;
    }

    @Override
    public boolean isInTargetState(Actor subject) {
        return !subject.isInCombat() && subject.getArea().equals(getArea());
    }

    @Override
    public Area getTargetArea(Actor subject) {
        return getArea();
    }

    private Area getArea() {
        if (area == null) throw new GameDataException("BehaviorMove area has not been resolved");
        return area;
    }

}
