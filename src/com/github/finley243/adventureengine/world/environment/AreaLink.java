package com.github.finley243.adventureengine.world.environment;

public class AreaLink {

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
    private final RelativeHeight height;
    private final AreaLinkType type;
    private final int distance;
    private final String moveNameOverride;

    public AreaLink(String areaID, RelativeHeight height, AreaLinkType type, int distance, String moveNameOverride) {
        this.areaID = areaID;
        this.height = height;
        this.type = type;
        this.distance = distance;
        this.moveNameOverride = moveNameOverride;
    }

    public String getAreaID() {
        return areaID;
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

    public String getMoveNameOverride() {
        return moveNameOverride;
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

}
