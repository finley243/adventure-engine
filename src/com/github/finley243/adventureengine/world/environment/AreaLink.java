package com.github.finley243.adventureengine.world.environment;

import com.github.finley243.adventureengine.world.object.ObjectObstruction;

public class AreaLink {

    public enum RelativeDirection {
        NORTH, SOUTH, EAST, WEST, NORTHEAST, NORTHWEST, SOUTHEAST, SOUTHWEST
    }

    // ABOVE/BELOW = can climb up and jump down (e.g. raised front step)
    // ABOVE_HIGH/BELOW_HIGH = cannot climb, jumping down will cause injury (e.g. second story balcony, maintenance catwalk)
    // ABOVE_EXTREME/BELOW_EXTREME = cannot climb, jumping down will cause death (e.g. rooftop of tall building)
    public enum RelativeHeight {
        EQUAL, ABOVE, BELOW, ABOVE_HIGH, BELOW_HIGH, ABOVE_EXTREME, BELOW_EXTREME
    }

    private final String areaID;
    // 1 = north, -1 = south, 0 = equal
    private final RelativeDirection relativeDirection;
    private final RelativeHeight relativeHeight;

    public AreaLink(String areaID, RelativeDirection relativeDirection, RelativeHeight relativeHeight) {
        this.areaID = areaID;
        this.relativeDirection = relativeDirection;
        this.relativeHeight = relativeHeight;
    }

    public String getAreaID() {
        return areaID;
    }

    public RelativeDirection getRelativeDirection() {
        return relativeDirection;
    }

    public RelativeHeight getRelativeHeight() {
        return relativeHeight;
    }

    public int heightChange() {
        switch(relativeHeight) {
            case ABOVE:
                return 1;
            case ABOVE_HIGH:
                return 2;
            case ABOVE_EXTREME:
                return 3;
            case BELOW:
                return -1;
            case BELOW_HIGH:
                return -2;
            case BELOW_EXTREME:
                return -3;
            case EQUAL:
            default:
                return 0;
        }
    }

    public int northSouthChange() {
        switch(relativeDirection) {
            case NORTH:
            case NORTHEAST:
            case NORTHWEST:
                return 1;
            case SOUTH:
            case SOUTHEAST:
            case SOUTHWEST:
                return -1;
            case EAST:
            case WEST:
            default:
                return 0;
        }
    }

    public int eastWestChange() {
        switch(relativeDirection) {
            case EAST:
            case NORTHEAST:
            case SOUTHEAST:
                return 1;
            case WEST:
            case NORTHWEST:
            case SOUTHWEST:
                return -1;
            case NORTH:
            case SOUTH:
            default:
                return 0;
        }
    }

    // If directions are opposites, returns null
    public static RelativeDirection combinedDirection(RelativeDirection d1, RelativeDirection d2) {
        // Check exact match
        if (d1 == d2) return d1;
        // Check combinations of diagonals
        if (d1 == RelativeDirection.NORTHWEST && d2 == RelativeDirection.SOUTHWEST || d2 == RelativeDirection.NORTHWEST && d1 == RelativeDirection.SOUTHWEST) {
            return RelativeDirection.WEST;
        } else if (d1 == RelativeDirection.NORTHEAST && d2 == RelativeDirection.SOUTHEAST || d2 == RelativeDirection.NORTHEAST && d1 == RelativeDirection.SOUTHEAST) {
            return RelativeDirection.EAST;
        } else if (d1 == RelativeDirection.NORTHWEST && d2 == RelativeDirection.NORTHEAST || d2 == RelativeDirection.NORTHWEST && d1 == RelativeDirection.NORTHEAST) {
            return RelativeDirection.NORTH;
        } else if (d1 == RelativeDirection.SOUTHEAST && d2 == RelativeDirection.SOUTHWEST || d2 == RelativeDirection.SOUTHEAST && d1 == RelativeDirection.SOUTHWEST) {
            return RelativeDirection.SOUTH;
            // Check combinations of non-diagonals
        } else if (d1 == RelativeDirection.NORTH && d2 == RelativeDirection.WEST || d2 == RelativeDirection.NORTH && d1 == RelativeDirection.WEST) {
            return RelativeDirection.NORTHWEST;
        } else if (d1 == RelativeDirection.NORTH && d2 == RelativeDirection.EAST || d2 == RelativeDirection.NORTH && d1 == RelativeDirection.EAST) {
            return RelativeDirection.NORTHEAST;
        } else if (d1 == RelativeDirection.SOUTH && d2 == RelativeDirection.WEST || d2 == RelativeDirection.SOUTH && d1 == RelativeDirection.WEST) {
            return RelativeDirection.SOUTHWEST;
        } else if (d1 == RelativeDirection.SOUTH && d2 == RelativeDirection.EAST || d2 == RelativeDirection.SOUTH && d1 == RelativeDirection.EAST) {
            return RelativeDirection.SOUTHEAST;
            // Check combinations of diagonal with non-diagonal
        } else if (d1 == RelativeDirection.NORTHWEST && (d2 == RelativeDirection.NORTH || d2 == RelativeDirection.WEST) || d2 == RelativeDirection.NORTHWEST && (d1 == RelativeDirection.NORTH || d1 == RelativeDirection.WEST)) {
            return RelativeDirection.NORTHWEST;
        } else if (d1 == RelativeDirection.NORTHEAST && (d2 == RelativeDirection.NORTH || d2 == RelativeDirection.EAST) || d2 == RelativeDirection.NORTHEAST && (d1 == RelativeDirection.NORTH || d1 == RelativeDirection.EAST)) {
            return RelativeDirection.NORTHEAST;
        } else if (d1 == RelativeDirection.SOUTHWEST && (d2 == RelativeDirection.SOUTH || d2 == RelativeDirection.WEST) || d2 == RelativeDirection.SOUTHWEST && (d1 == RelativeDirection.SOUTH || d1 == RelativeDirection.WEST)) {
            return RelativeDirection.SOUTHWEST;
        } else if (d1 == RelativeDirection.SOUTHEAST && (d2 == RelativeDirection.SOUTH || d2 == RelativeDirection.EAST) || d2 == RelativeDirection.SOUTHEAST && (d1 == RelativeDirection.SOUTH || d1 == RelativeDirection.EAST)) {
            return RelativeDirection.SOUTHEAST;
        } else {
            return null;
        }
    }

}
