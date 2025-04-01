package com.github.finley243.adventureengine.world.path;

import com.github.finley243.adventureengine.actor.SenseType;
import com.github.finley243.adventureengine.world.environment.Area;

public class PathDataArea extends PathData {

    private final Area area;

    public PathDataArea(Area area) {
        this.area = area;
    }

    @Override
    public Obstruction getObstruction(SenseType senseType) {
        return null;
    }

    public Area getArea() {
        return area;
    }

}
