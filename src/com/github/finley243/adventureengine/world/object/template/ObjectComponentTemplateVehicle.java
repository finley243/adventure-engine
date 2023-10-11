package com.github.finley243.adventureengine.world.object.template;

public class ObjectComponentTemplateVehicle extends ObjectComponentTemplate {

    private final String vehicleType;

    public ObjectComponentTemplateVehicle(boolean startEnabled, boolean actionsRestricted, String vehicleType) {
        super(startEnabled, actionsRestricted);
        this.vehicleType = vehicleType;
    }

    public String getVehicleType() {
        return vehicleType;
    }

}
