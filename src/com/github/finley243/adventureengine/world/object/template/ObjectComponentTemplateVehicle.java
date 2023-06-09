package com.github.finley243.adventureengine.world.object.template;

import com.github.finley243.adventureengine.Game;

public class ObjectComponentTemplateVehicle extends ObjectComponentTemplate {

    private final String vehicleType;
    // Menu category under which move actions will be placed
    private final String moveMenuName;

    public ObjectComponentTemplateVehicle(Game game, String ID, boolean startEnabled, boolean actionsRestricted, String name, String vehicleType, String moveMenuName) {
        super(game, ID, startEnabled, actionsRestricted, name);
        this.vehicleType = vehicleType;
        this.moveMenuName = moveMenuName;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public String getMoveMenuName() {
        return moveMenuName;
    }

}
