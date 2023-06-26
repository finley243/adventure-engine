package com.github.finley243.adventureengine.world.environment;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.GameInstanced;

import java.util.Map;
import java.util.Set;

public class LinkType extends GameInstanced {

    private final boolean isVisible;
    private final String actorMoveAction;
    private final Set<AreaLink.DistanceCategory> actorMoveDistances;
    private final Map<String, String> vehicleMoveActions;
    private final Map<String, Set<AreaLink.DistanceCategory>> vehicleMoveDistances;

    public LinkType(Game game, String ID, boolean isVisible, String actorMoveAction, Set<AreaLink.DistanceCategory> actorMoveDistances, Map<String, String> vehicleMoveActions, Map<String, Set<AreaLink.DistanceCategory>> vehicleMoveDistances) {
        super(game, ID);
        this.isVisible = isVisible;
        this.actorMoveAction = actorMoveAction;
        this.actorMoveDistances = actorMoveDistances;
        this.vehicleMoveActions = vehicleMoveActions;
        this.vehicleMoveDistances = vehicleMoveDistances;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public String getActorMoveAction() {
        return actorMoveAction;
    }

    public Set<AreaLink.DistanceCategory> getActorMoveDistances() {
        return actorMoveDistances;
    }

    public boolean allowAllActorDistances() {
        return actorMoveDistances.isEmpty();
    }

    public String getVehicleMoveAction(String vehicleType) {
        return vehicleMoveActions.get(vehicleType);
    }

    public Set<AreaLink.DistanceCategory> getVehicleMoveDistances(String vehicleType) {
        return vehicleMoveDistances.get(vehicleType);
    }

    public boolean allowAllVehicleDistances(String vehicleType) {
        if (!vehicleMoveDistances.containsKey(vehicleType)) {
            return false;
        }
        return vehicleMoveDistances.get(vehicleType).isEmpty();
    }

}
