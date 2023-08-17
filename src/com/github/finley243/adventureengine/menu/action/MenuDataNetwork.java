package com.github.finley243.adventureengine.menu.action;

import com.github.finley243.adventureengine.network.NetworkNode;

public class MenuDataNetwork extends MenuData {

    public final NetworkNode node;

    public MenuDataNetwork(NetworkNode node) {
        this.node = node;
    }

}
