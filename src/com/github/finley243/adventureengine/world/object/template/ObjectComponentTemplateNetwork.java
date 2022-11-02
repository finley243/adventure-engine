package com.github.finley243.adventureengine.world.object.template;

import com.github.finley243.adventureengine.Game;

public class ObjectComponentTemplateNetwork extends ObjectComponentTemplate {

    private final String networkID;

    public ObjectComponentTemplateNetwork(Game game, String ID, boolean startEnabled, String networkID) {
        super(game, ID, startEnabled);
        this.networkID = networkID;
    }

    public String getNetworkID() {
        return networkID;
    }

}
