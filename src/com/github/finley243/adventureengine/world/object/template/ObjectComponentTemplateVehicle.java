package com.github.finley243.adventureengine.world.object.template;

import com.github.finley243.adventureengine.Game;

public class ObjectComponentTemplateVehicle extends ObjectComponentTemplate {

    private final String vehicleType;

    public ObjectComponentTemplateVehicle(Game game, String ID, boolean startEnabled, boolean actionsRestricted, String name, String vehicleType) {
        super(game, ID, startEnabled, actionsRestricted, name);
        this.vehicleType = vehicleType;
    }

    public String getVehicleType() {
        return vehicleType;
    }

}
