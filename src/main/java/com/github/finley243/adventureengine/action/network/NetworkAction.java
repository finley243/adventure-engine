package com.github.finley243.adventureengine.action.network;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.event.SensoryEventDispatcher;
import com.github.finley243.adventureengine.script.ScriptRuntime;

public abstract class NetworkAction extends Action {

    public NetworkAction(ScriptRuntime scriptRuntime, SensoryEventDispatcher sensoryEventDispatcher) {
        super(scriptRuntime, sensoryEventDispatcher);
    }

    public abstract float networkDetectionChance();

}
