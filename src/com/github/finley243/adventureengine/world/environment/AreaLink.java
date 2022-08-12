package com.github.finley243.adventureengine.world.environment;

import com.github.finley243.adventureengine.textgen.Phrases;

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

    public enum CompassDirection {
        N("north"), S("south"), W("west"), E("east"), NW("northwest"), NE("northeast"), SW("southwest"), SE("southeast");

        public final String name;

        CompassDirection(String name) {
            this.name = name;
        }
    }

    private final String areaID;
    // 1 = north, -1 = south, 0 = equal
    private final RelativeHeight height;
    private final AreaLinkType type;
    private final CompassDirection direction;
    private final int distance;
    private final String moveNameOverride;
    private final String movePhraseOverride;

    public AreaLink(String areaID, RelativeHeight height, CompassDirection direction, AreaLinkType type, int distance, String moveNameOverride, String movePhraseOverride) {
        this.areaID = areaID;
        this.height = height;
        this.direction = direction;
        this.type = type;
        this.distance = distance;
        this.moveNameOverride = moveNameOverride;
        this.movePhraseOverride = movePhraseOverride;
    }

    public String getAreaID() {
        return areaID;
    }

    public RelativeHeight getHeight() {
        return height;
    }

    public CompassDirection getDirection() {
        return direction;
    }

    public AreaLinkType getType() {
        return type;
    }

    public int getDistance() {
        return distance;
    }

    public String getMoveName(Area currentArea) {
        if (moveNameOverride != null) {
            return "(" + getDirection() + ") " + moveNameOverride;
        } else if (type == AreaLinkType.CORNER) {
            return "(" + getDirection() + ") " + "turn corner";
        } else if (!currentArea.getRoom().equals(currentArea.game().data().getArea(areaID).getRoom())) {
            return "(" + getDirection() + ") " + currentArea.game().data().getArea(areaID).getRoom().getName();
        } else {
            return "(" + getDirection() + ") " + currentArea.game().data().getArea(areaID).getName();
        }
    }

    public String getMovePhrase(Area currentArea) {
        if (movePhraseOverride != null) {
            return movePhraseOverride;
        } else if (type == AreaLinkType.CORNER) {
            return Phrases.get("moveCorner");
        } else {
            return currentArea.game().data().getArea(areaID).getMovePhrase();
        }
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
