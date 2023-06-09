package com.github.finley243.adventureengine.world.environment;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.GameInstanced;

import java.util.Map;

public class LinkType extends GameInstanced {

    private final boolean isVisible;
    private final String actorMoveAction;
    private final Map<String, String> vehicleMoveActions;

    public LinkType(Game game, String ID, boolean isVisible, String actorMoveAction, Map<String, String> vehicleMoveActions) {
        super(game, ID);
        this.isVisible = isVisible;
        this.actorMoveAction = actorMoveAction;
        this.vehicleMoveActions = vehicleMoveActions;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public String getActorMoveAction() {
        return actorMoveAction;
    }

    public String getVehicleMoveAction(String vehicleType) {
        return vehicleMoveActions.get(vehicleType);
    }

}
