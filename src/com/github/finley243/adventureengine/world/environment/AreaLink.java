package com.github.finley243.adventureengine.world.environment;

public class AreaLink {

    public enum RelativeDirection {
        NORTH("N"), SOUTH("S"), EAST("E"), WEST("W"), NORTHEAST("NE"), NORTHWEST("NW"), SOUTHEAST("SE"), SOUTHWEST("SW");

        public final String tag;

        RelativeDirection(String tag) {
            this.tag = tag;
        }
    }

    // ABOVE/BELOW = can climb up and jump down (e.g. raised front step)
    // ABOVE_HIGH/BELOW_HIGH = cannot climb, jumping down will cause injury (e.g. second story balcony, maintenance catwalk)
    // ABOVE_EXTREME/BELOW_EXTREME = cannot climb, jumping down will cause death (e.g. rooftop of tall building)
    public enum RelativeHeight {
        EQUAL(null), ABOVE("above"), BELOW("below"), ABOVE_HIGH("above"), BELOW_HIGH("below"), ABOVE_EXTREME("far above"), BELOW_EXTREME("far below");

        public final String description;

        RelativeHeight(String description) {
            this.description = description;
        }
    }

    public enum AreaLinkType {
        DIRECT(true, true), CORNER(false, true), FAR(true, false);

        public final boolean isVisible;
        public final boolean isMovable;

        AreaLinkType(boolean isVisible, boolean isMovable) {
            this.isVisible = isVisible;
            this.isMovable = isMovable;
        }
    }

    private final String areaID;
    // 1 = north, -1 = south, 0 = equal
    private final RelativeDirection direction;
    private final RelativeHeight height;
    private final AreaLinkType type;
    private final int distance;

    public AreaLink(String areaID, RelativeDirection direction, RelativeHeight height, AreaLinkType type, int distance) {
        this.areaID = areaID;
        this.direction = direction;
        this.height = height;
        this.type = type;
        this.distance = distance;
    }

    public String getAreaID() {
        return areaID;
    }

    public RelativeDirection getDirection() {
        return direction;
    }

    public RelativeHeight getHeight() {
        return height;
    }

    public AreaLinkType getType() {
        return type;
    }

    public int getDistance() {
        return distance;
    }

    public int heightChange() {
        switch(height) {
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
        switch(direction) {
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
        switch(direction) {
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
    /*public static RelativeDirection combinedDirection(RelativeDirection d1, RelativeDirection d2) {
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
    }*/

}
