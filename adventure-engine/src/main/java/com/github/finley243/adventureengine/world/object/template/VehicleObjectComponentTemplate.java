package com.github.finley243.adventureengine.world.object.template;

public class VehicleObjectComponentTemplate extends ObjectComponentTemplate {

    private final String vehicleType;

    public VehicleObjectComponentTemplate(boolean startEnabled, boolean actionsRestricted, String vehicleType) {
        super(startEnabled, actionsRestricted);
        this.vehicleType = vehicleType;
    }

    public String getVehicleType() {
        return vehicleType;
    }

}
