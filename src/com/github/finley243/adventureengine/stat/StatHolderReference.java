package com.github.finley243.adventureengine.stat;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;

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

    public StatHolder getHolder(Game game, Actor subject, Actor target) {
        switch (holderType) {
            case "actor":
                return game.data().getActor(holderID);
            case "object":
                return game.data().getObject(holderID);
            case "objectComponent":
                return game.data().getObject(holderID).getComponent(holderLocalID);
            case "item":
                return game.data().getItemState(holderID);
            case "player":
                return game.data().getPlayer();
            case "target":
                return target;
            case "subject":
            default:
                return subject;
        }
    }

}
