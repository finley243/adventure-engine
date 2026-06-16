package com.github.finley243.adventureengine.action.network;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.action.ActionDependencies;
import com.github.finley243.adventureengine.actor.Actor;

public abstract class NetworkAction extends Action {

    public NetworkAction(Actor subject, ActionDependencies dependencies) {
        super(subject, dependencies);
    }

    public abstract float networkDetectionChance();

}
