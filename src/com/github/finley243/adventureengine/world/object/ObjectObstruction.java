package com.github.finley243.adventureengine.world.object;

import com.github.finley243.adventureengine.world.environment.AreaLink;

public class ObjectObstruction extends WorldObject {

    public enum ObstructionDirection {
        NORTH, SOUTH, EAST, WEST
    }

    public enum ObstructionType {
        PARTIAL, FULL
    }

    private ObstructionDirection direction;
    private ObstructionType type;

    public ObjectObstruction(String name, String description, ObstructionDirection direction, ObstructionType type) {
        super(name, description);
        this.direction = direction;
        this.type = type;
    }

    public ObstructionDirection getDirection() {
        return direction;
    }

    public ObstructionType getType() {
        return type;
    }

    // Is this area obstructed from the given direction
    public boolean obstructsFrom(AreaLink.RelativeDirection direction) {
        switch(direction) {
            case NORTH:
                return this.direction == ObstructionDirection.NORTH;
            case SOUTH:
                return this.direction == ObstructionDirection.SOUTH;
            case EAST:
                return this.direction == ObstructionDirection.EAST;
            case WEST:
                return this.direction == ObstructionDirection.WEST;
            case NORTHEAST:
                return this.direction == ObstructionDirection.NORTH || this.direction == ObstructionDirection.EAST;
            case NORTHWEST:
                return this.direction == ObstructionDirection.NORTH || this.direction == ObstructionDirection.WEST;
            case SOUTHEAST:
                return this.direction == ObstructionDirection.SOUTH || this.direction == ObstructionDirection.EAST;
            case SOUTHWEST:
                return this.direction == ObstructionDirection.SOUTH || this.direction == ObstructionDirection.WEST;
            default:
                return false;
        }
    }

    // Is the area in the given direction obstructed from this area
    /*public boolean obstructsFromPost(AreaLink.RelativeDirection direction) {
        switch(direction) {
            case NORTH:
                return this.direction == ObstructionDirection.SOUTH;
            case SOUTH:
                return this.direction == ObstructionDirection.NORTH;
            case EAST:
                return this.direction == ObstructionDirection.WEST;
            case WEST:
                return this.direction == ObstructionDirection.EAST;
            case NORTHEAST:
                return this.direction == ObstructionDirection.NORTH || this.direction == ObstructionDirection.EAST;
            case NORTHWEST:
                return this.direction == ObstructionDirection.NORTH || this.direction == ObstructionDirection.WEST;
            case SOUTHEAST:
                return this.direction == ObstructionDirection.SOUTH || this.direction == ObstructionDirection.EAST;
            case SOUTHWEST:
                return this.direction == ObstructionDirection.SOUTH || this.direction == ObstructionDirection.WEST;
            default:
                return false;
        }
    }*/

}
