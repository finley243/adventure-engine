package com.github.finley243.adventureengine.action.network;

import com.github.finley243.adventureengine.action.Action;

public abstract class NetworkAction extends Action {

    public NetworkAction() {}

    public abstract float networkDetectionChance();

}
