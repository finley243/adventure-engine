package com.github.finley243.adventureengine.world.path;

import com.github.finley243.adventureengine.actor.SenseType;

public abstract class PathData {

    public PathData() {
    }

    public abstract Obstruction getObstruction(SenseType senseType);

    // visibilityMod can affect both hit chance and detection chance
    public record Obstruction(boolean isVisible, boolean isReachable, float visibilityMod) {}

}
