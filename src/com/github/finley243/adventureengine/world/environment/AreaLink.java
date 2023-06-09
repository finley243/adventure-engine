package com.github.finley243.adventureengine.world.environment;

import com.github.finley243.adventureengine.Game;

public class AreaLink {

    public enum DistanceCategory {
        NEAR, CLOSE, FAR, DISTANT
    }

    public enum CompassDirection {
        N("north"), S("south"), W("west"), E("east"), NW("northwest"), NE("northeast"), SW("southwest"), SE("southeast");

        public final String name;

        CompassDirection(String name) {
            this.name = name;
        }
    }

    private final String areaID;
    private final String type;
    private final CompassDirection direction;
    private final DistanceCategory distance;

    public AreaLink(String areaID, String type, CompassDirection direction, DistanceCategory distance) {
        this.areaID = areaID;
        this.type = type;
        this.direction = direction;
        this.distance = distance;
    }

    public String getAreaID() {
        return areaID;
    }

    public String getType() {
        return type;
    }

    public boolean isMovable(Game game) {
        if (game.data().getLinkType(getType()).getActorMoveAction() == null) {
            return false;
        }
        return getDistance() == DistanceCategory.NEAR || getDistance() == DistanceCategory.CLOSE;
    }

    public boolean isVehicleMovable(Game game, String vehicleType) {
        return game.data().getLinkType(getType()).getVehicleMoveAction(vehicleType) != null;
    }

    public CompassDirection getDirection() {
        return direction;
    }

    public DistanceCategory getDistance() {
        return distance;
    }

}
