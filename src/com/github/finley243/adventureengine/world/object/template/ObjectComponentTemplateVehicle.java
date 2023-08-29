package com.github.finley243.adventureengine.world.object.template;

public class ObjectComponentTemplateVehicle extends ObjectComponentTemplate {

    private final String vehicleType;
    // Menu category under which move actions will be placed
    private final String moveMenuName;

    public ObjectComponentTemplateVehicle(boolean startEnabled, boolean actionsRestricted, String vehicleType, String moveMenuName) {
        super(startEnabled, actionsRestricted);
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
