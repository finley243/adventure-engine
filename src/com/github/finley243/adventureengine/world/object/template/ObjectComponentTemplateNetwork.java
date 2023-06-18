package com.github.finley243.adventureengine.world.object.template;

import com.github.finley243.adventureengine.Game;

public class ObjectComponentTemplateNetwork extends ObjectComponentTemplate {

    private final String networkID;

    public ObjectComponentTemplateNetwork(Game game, boolean startEnabled, boolean actionsRestricted, String name, String networkID) {
        super(game, startEnabled, actionsRestricted, name);
        this.networkID = networkID;
    }

    public String getNetworkID() {
        return networkID;
    }

}
