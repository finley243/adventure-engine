package com.github.finley243.adventureengine.stat;

import com.github.finley243.adventureengine.ContextScript;

public class StatHolderReference {

    private final String holderType;
    private final String holderID;
    private final String subType;
    // Local ID is used for components
    private final String subID;

    public StatHolderReference(String holderType, String holderID, String subType, String subID) {
        this.holderType = holderType;
        this.holderID = holderID;
        this.subType = subType;
        this.subID = subID;
    }

    public StatHolder getHolder(ContextScript context) {
        StatHolder parentHolder = getParentHolder(context);
        if (subType == null) {
            return parentHolder;
        }
        StatHolder subHolder = parentHolder.getSubHolder(subType, subID);
        if (subHolder == null) {
            throw new IllegalArgumentException("StatHolder sub-type '" + subType + "' does not exist on holder type '" + holderType + "'");
        }
        return subHolder;
    }

    private StatHolder getParentHolder(ContextScript context) {
        switch (holderType) {
            case "object":
                return context.game().data().getObject(holderID);
            case "parentObject":
                return context.getParentObject();
            case "item":
                return context.game().data().getItemState(holderID);
            case "parentItem":
                return context.getParentItem();
            case "area":
                return context.game().data().getArea(holderID);
            case "room":
                return context.game().data().getRoom(holderID);
            case "scene":
                return context.game().data().getScene(holderID);
            case "actor":
                return context.game().data().getActor(holderID);
            case "player":
                return context.game().data().getPlayer();
            case "target":
                return context.getTarget();
            case "subject":
            default:
                return context.getSubject();
        }
    }

}
