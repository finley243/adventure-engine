package com.github.finley243.adventureengine.stat;

import com.github.finley243.adventureengine.ContextScript;

public class StatHolderReference {

    private final String holderType;
    private final String holderID;
    // Local ID is used for components
    private final String holderLocalID;

    public StatHolderReference(String holderType, String holderID, String holderLocalID) {
        this.holderType = holderType;
        this.holderID = holderID;
        this.holderLocalID = holderLocalID;
    }

    public StatHolder getHolder(ContextScript context) {
        switch (holderType) {
            case "actor":
                return context.game().data().getActor(holderID);
            case "object":
                return context.game().data().getObject(holderID);
            case "objectComponent":
                return context.game().data().getObject(holderID).getComponent(holderLocalID);
            case "item":
                return context.game().data().getItemState(holderID);
            case "area":
                return context.game().data().getArea(holderID);
            case "room":
                return context.game().data().getRoom(holderID);
            case "scene":
                return context.game().data().getScene(holderID);
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
