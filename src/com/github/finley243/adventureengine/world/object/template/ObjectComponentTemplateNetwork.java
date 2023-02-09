package com.github.finley243.adventureengine.world.object.template;

import com.github.finley243.adventureengine.Game;

public class ObjectComponentTemplateNetwork extends ObjectComponentTemplate {

    private final String networkID;

    public ObjectComponentTemplateNetwork(Game game, String ID, boolean startEnabled, String name, String networkID) {
        super(game, ID, startEnabled, name);
        this.networkID = networkID;
    }

    public String getNetworkID() {
        return networkID;
    }

}
