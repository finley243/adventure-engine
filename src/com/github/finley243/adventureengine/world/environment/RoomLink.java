package com.github.finley243.adventureengine.world.environment;

public class RoomLink {

    private final String roomID;
    private final AreaLink.CompassDirection direction;
    private final AreaLink.DistanceCategory distance;

    public RoomLink(String roomID, AreaLink.CompassDirection direction, AreaLink.DistanceCategory distance) {
        this.roomID = roomID;
        this.direction = direction;
        this.distance = distance;
    }

    public String getRoomID() {
        return roomID;
    }

    public AreaLink.CompassDirection getDirection() {
        return direction;
    }

    public AreaLink.DistanceCategory getDistance() {
        return distance;
    }

}
