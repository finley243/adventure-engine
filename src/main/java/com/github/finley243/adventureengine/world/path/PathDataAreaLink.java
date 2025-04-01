package com.github.finley243.adventureengine.world.path;

import com.github.finley243.adventureengine.actor.SenseType;
import com.github.finley243.adventureengine.world.environment.AreaLink;

public class PathDataAreaLink extends PathData {

    private final AreaLink link;

    public PathDataAreaLink(AreaLink link) {
        this.link = link;
    }

    @Override
    public Obstruction getObstruction(SenseType senseType) {
        return null;
    }

}
