package com.github.finley243.adventureengine.actor;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.menu.MenuManager;

import java.util.List;

public class ControllerPlayer implements ControllerComponent {

    private final MenuManager menuManager;
    private final Actor actor;

    public ControllerPlayer(Actor actor) {
        this.actor = actor;
        menuManager = new MenuManager();
    }

    @Override
    public Action chooseAction(List<Action> actions) {
        return menuManager.actionMenu(actions, actor);
    }

    @Override
    public void onStartTurn() {

    }

    @Override
    public void onStartAction() {

    }

}
