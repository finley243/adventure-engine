package com.github.finley243.adventureengine.world.object.template;

public class ObjectComponentTemplateNetwork extends ObjectComponentTemplate {

    private final String networkID;

    public ObjectComponentTemplateNetwork(String networkID) {
        this.networkID = networkID;
    }

    public String getNetworkID() {
        return networkID;
    }

}
