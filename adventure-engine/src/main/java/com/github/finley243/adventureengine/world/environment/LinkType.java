package com.github.finley243.adventureengine.world.environment;

import com.github.finley243.adventureengine.GameInstanced;
import com.github.finley243.adventureengine.action.ActionTemplate;

import java.util.Map;
import java.util.Set;

public class LinkType extends GameInstanced {

    private final boolean isVisible;
    private final ActionTemplate actorMoveAction;
    private final Set<AreaLink.DistanceCategory> actorMoveDistances;
    private final Map<String, ActionTemplate> vehicleMoveActions;
    private final Map<String, Set<AreaLink.DistanceCategory>> vehicleMoveDistances;

    public LinkType(String ID, boolean isVisible, ActionTemplate actorMoveAction, Set<AreaLink.DistanceCategory> actorMoveDistances, Map<String, ActionTemplate> vehicleMoveActions, Map<String, Set<AreaLink.DistanceCategory>> vehicleMoveDistances) {
        super(ID);
        this.isVisible = isVisible;
        this.actorMoveAction = actorMoveAction;
        this.actorMoveDistances = actorMoveDistances;
        this.vehicleMoveActions = vehicleMoveActions;
        this.vehicleMoveDistances = vehicleMoveDistances;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public ActionTemplate getActorMoveAction() {
        return actorMoveAction;
    }

    public Set<AreaLink.DistanceCategory> getActorMoveDistances() {
        return actorMoveDistances;
    }

    public boolean allowAllActorDistances() {
        return actorMoveDistances.isEmpty();
    }

    public ActionTemplate getVehicleMoveAction(String vehicleType) {
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
