package com.github.finley243.adventureengine.actor.component;

import com.github.finley243.adventureengine.action.Action;

import java.util.List;

public interface ActionComponent {

    Action chooseAction(List<Action> actions);

}
