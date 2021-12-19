package com.github.finley243.adventureengine.actor;

import com.github.finley243.adventureengine.action.Action;

import java.util.List;

public interface BehaviorComponent {

    Action chooseAction(List<Action> choices);

    void onStartTurn();

    void onStartAction();

}
