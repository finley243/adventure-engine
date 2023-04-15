package com.github.finley243.adventureengine.world.environment;

public class AreaLink {

    public enum AreaLinkType {
        BASIC(true), CORNER(false), OBSTRUCTED(false);

        public final boolean isVisible;

        AreaLinkType(boolean isVisible) {
            this.isVisible = isVisible;
        }
    }

    public enum DistanceCategory {
        NEAR(true),
        CLOSE(true),
        FAR(false),
        DISTANT(false);

        public final boolean isMovable;

        DistanceCategory(boolean isMovable) {
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
    private final String type;
    private final CompassDirection direction;
    private final DistanceCategory distance;
    private final String moveNameOverride;
    private final String movePhraseOverride;

    public AreaLink(String areaID, String type, CompassDirection direction, DistanceCategory distance, String moveNameOverride, String movePhraseOverride) {
        this.areaID = areaID;
        this.type = type;
        this.direction = direction;
        this.distance = distance;
        this.moveNameOverride = moveNameOverride;
        this.movePhraseOverride = movePhraseOverride;
    }

    public String getAreaID() {
        return areaID;
    }

    public String getType() {
        return type;
    }

    public CompassDirection getDirection() {
        return direction;
    }

    public DistanceCategory getDistance() {
        return distance;
    }

    public String getMoveName(Area currentArea) {
        /*if (moveNameOverride != null) {
            return "(" + getDirection() + ") " + moveNameOverride;
        } else if (!currentArea.getRoom().equals(currentArea.game().data().getArea(areaID).getRoom())) {
            if (type == AreaLinkType.CORNER) {
                return "(" + getDirection() + ") " + "turn corner to " + currentArea.game().data().getArea(areaID).getRoom().getName();
            } else {
                return "(" + getDirection() + ") " + currentArea.game().data().getArea(areaID).getRoom().getName();
            }
        } else {
            if (type == AreaLinkType.CORNER) {
                return "(" + getDirection() + ") " + "turn corner to " + currentArea.game().data().getArea(areaID).getName();
            } else {
                return "(" + getDirection() + ") " + currentArea.game().data().getArea(areaID).getName();
            }
        }*/
        if (moveNameOverride != null) {
            return "(" + getDirection() + ") " + moveNameOverride;
        } else {
            return null;
        }
    }

    public String getMovePhrase(Area currentArea) {
        if (movePhraseOverride != null) {
            return movePhraseOverride;
        //} else if (type == AreaLinkType.CORNER) {
            //return Phrases.get("moveCorner");
        } else {
            //return currentArea.game().data().getArea(areaID).getMovePhrase(currentArea);
            return null;
        }
    }

}
