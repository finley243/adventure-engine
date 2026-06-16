package com.github.finley243.adventureengine.action.network;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionDependencies;

public abstract class NetworkAction extends Action {

    public NetworkAction(ActionDependencies dependencies) {
        super(dependencies);
    }

    public abstract float networkDetectionChance();

}
