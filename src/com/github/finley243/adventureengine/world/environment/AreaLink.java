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
        if (game.data().getLinkType(getType()).allowAllActorDistances()) {
            return true;
        }
        return game.data().getLinkType(getType()).getActorMoveDistances().contains(getDistance());
    }

    public boolean isVehicleMovable(Game game, String vehicleType) {
        if (game.data().getLinkType(getType()).getVehicleMoveAction(vehicleType) == null) {
            return false;
        }
        if (game.data().getLinkType(getType()).allowAllVehicleDistances(vehicleType)) {
            return true;
        }
        return game.data().getLinkType(getType()).getVehicleMoveDistances(vehicleType).contains(getDistance());
    }

    public CompassDirection getDirection() {
        return direction;
    }

    public DistanceCategory getDistance() {
        return distance;
    }

    public static DistanceCategory combinedDistance(DistanceCategory distance1, DistanceCategory distance2) {
        if (distance1 == null || distance2 == null) throw new IllegalArgumentException("One or more provided distances is null");
        switch (distance1) {
            case NEAR -> {
                return switch (distance2) {
                    case NEAR -> DistanceCategory.CLOSE;
                    case CLOSE, FAR -> DistanceCategory.FAR;
                    case DISTANT -> DistanceCategory.DISTANT;
                };
            }
            case CLOSE -> {
                return switch (distance2) {
                    case NEAR, CLOSE, FAR -> DistanceCategory.FAR;
                    case DISTANT -> DistanceCategory.DISTANT;
                };
            }
            case FAR -> {
                return switch (distance2) {
                    case NEAR, CLOSE -> DistanceCategory.FAR;
                    case FAR, DISTANT -> DistanceCategory.DISTANT;
                };
            }
            case DISTANT -> {
                return DistanceCategory.DISTANT;
            }
        }
        return null;
    }

}
