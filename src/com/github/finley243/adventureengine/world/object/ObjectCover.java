package com.github.finley243.adventureengine.world.object;

import com.github.finley243.adventureengine.world.environment.AreaLink;

public class ObjectCover extends WorldObject {

    public enum CoverDirection {
        NORTH, SOUTH, EAST, WEST
    }

    private CoverDirection direction;

    public ObjectCover(String name, String description, CoverDirection direction) {
        super(name, description);
        this.direction = direction;
    }

    public CoverDirection getDirection() {
        return direction;
    }

    // Is this area obstructed from the given direction
    public boolean obstructsFrom(AreaLink.RelativeDirection direction) {
        switch(direction) {
            case NORTH:
                return this.direction == CoverDirection.SOUTH;
            case SOUTH:
                return this.direction == CoverDirection.NORTH;
            case EAST:
                return this.direction == CoverDirection.WEST;
            case WEST:
                return this.direction == CoverDirection.EAST;
            case NORTHEAST:
                return this.direction == CoverDirection.SOUTH || this.direction == CoverDirection.WEST;
            case NORTHWEST:
                return this.direction == CoverDirection.SOUTH || this.direction == CoverDirection.EAST;
            case SOUTHEAST:
                return this.direction == CoverDirection.NORTH || this.direction == CoverDirection.WEST;
            case SOUTHWEST:
                return this.direction == CoverDirection.NORTH || this.direction == CoverDirection.EAST;
            default:
                return false;
        }
    }

}
