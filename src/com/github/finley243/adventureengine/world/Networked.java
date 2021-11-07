package com.github.finley243.adventureengine.world;

import com.github.finley243.adventureengine.network.action.NetworkAction;

import java.util.List;

public interface Networked {

    String getName();
    
    String getNetwork();

    List<NetworkAction> networkActions();

}
