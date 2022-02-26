package com.github.finley243.adventureengine.network;

import java.util.ArrayList;
import java.util.List;

public class SubNetwork {

    private final List<String> connected;
    private final int securityLevel;

    private boolean isBreached;

    public SubNetwork(int securityLevel, List<String> connected) {
        this.connected = connected;
        this.securityLevel = securityLevel;
    }

    public boolean isBreached() {
        return isBreached;
    }

}
