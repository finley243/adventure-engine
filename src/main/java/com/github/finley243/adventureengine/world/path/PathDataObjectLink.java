package com.github.finley243.adventureengine.world.path;

import com.github.finley243.adventureengine.actor.SenseType;
import com.github.finley243.adventureengine.world.object.WorldObject;

public class PathDataObjectLink extends PathData {

    private final WorldObject object;
    private final String linkID;

    public PathDataObjectLink(WorldObject object, String linkID) {
        this.object = object;
        this.linkID = linkID;
    }

    @Override
    public Obstruction getObstruction(SenseType senseType) {
        return null;
    }

}
