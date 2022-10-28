package com.github.finley243.adventureengine.world.object.template;

public class ObjectComponentTemplateNetwork extends ObjectComponentTemplate {

    private final String networkID;

    public ObjectComponentTemplateNetwork(String ID, boolean startEnabled, String networkID) {
        super(ID, startEnabled);
        this.networkID = networkID;
    }

    public String getNetworkID() {
        return networkID;
    }

}
