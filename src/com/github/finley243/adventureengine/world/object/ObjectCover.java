package com.github.finley243.adventureengine.world.object;

import com.github.finley243.adventureengine.world.environment.AreaLink;

public class ObjectCover extends WorldObject {

    public enum CoverDirection {
        NORTH(new AreaLink.RelativeDirection[]{AreaLink.RelativeDirection.NORTH, AreaLink.RelativeDirection.NORTHWEST, AreaLink.RelativeDirection.NORTHEAST}),
        SOUTH(new AreaLink.RelativeDirection[]{AreaLink.RelativeDirection.SOUTH, AreaLink.RelativeDirection.SOUTHWEST, AreaLink.RelativeDirection.SOUTHEAST}),
        EAST(new AreaLink.RelativeDirection[]{AreaLink.RelativeDirection.EAST, AreaLink.RelativeDirection.NORTHEAST, AreaLink.RelativeDirection.SOUTHEAST}),
        WEST(new AreaLink.RelativeDirection[]{AreaLink.RelativeDirection.WEST, AreaLink.RelativeDirection.NORTHWEST, AreaLink.RelativeDirection.SOUTHWEST});

        public final AreaLink.RelativeDirection[] obstructsTo;

        CoverDirection(AreaLink.RelativeDirection[] obstructsTo) {
            this.obstructsTo = obstructsTo;
        }
    }

    private CoverDirection direction;

    public ObjectCover(String ID, String name, String description, CoverDirection direction) {
        super(ID, name, description);
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

    public boolean obstructsTo(AreaLink.RelativeDirection direction) {
        switch(direction) {
            case NORTH:
                return this.direction == CoverDirection.NORTH;
            case SOUTH:
                return this.direction == CoverDirection.SOUTH;
            case EAST:
                return this.direction == CoverDirection.EAST;
            case WEST:
                return this.direction == CoverDirection.WEST;
            case NORTHEAST:
                return this.direction == CoverDirection.NORTH || this.direction == CoverDirection.EAST;
            case NORTHWEST:
                return this.direction == CoverDirection.NORTH || this.direction == CoverDirection.WEST;
            case SOUTHEAST:
                return this.direction == CoverDirection.SOUTH || this.direction == CoverDirection.EAST;
            case SOUTHWEST:
                return this.direction == CoverDirection.SOUTH || this.direction == CoverDirection.WEST;
            default:
                return false;
        }
    }

}
