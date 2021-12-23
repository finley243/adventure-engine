package com.github.finley243.adventureengine.world;

import com.github.finley243.adventureengine.action.network.NetworkAction;

import java.util.List;

public interface Networked {

    String getName();

    List<NetworkAction> networkActions();

}
