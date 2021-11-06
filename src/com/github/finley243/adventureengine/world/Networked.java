package com.github.finley243.adventureengine.world;

import com.github.finley243.adventureengine.network.action.NetworkAction;

import java.util.List;

public interface Networked {

    public String getName();
    
    public String getNetwork();

    public List<NetworkAction> networkActions();

}
