package com.github.finley243.adventureengine.world.object.component;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;

import java.util.List;

public interface ObjectComponent {

    List<Action> getActions(Actor subject);

    void setEnabled(boolean enabled);

    void onGameInit();

}
