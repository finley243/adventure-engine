package com.github.finley243.adventureengine.actor;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.event.SensoryEventDispatcher;

public class NPCController extends TurnController {

    public NPCController(Actor actor, SensoryEventDispatcher sensoryEventDispatcher) {
        super(actor, sensoryEventDispatcher);
    }

    @Override
    protected void onPostAction(Action action) {
        getActor().getBehaviorComponent().onPerformAction(action);
    }

}
