package com.github.finley243.adventureengine.actor;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.menu.MenuManager;

import java.util.List;

public class BehaviorPlayer implements BehaviorComponent {

    private final MenuManager menuManager;
    private final Actor actor;

    public BehaviorPlayer(Actor actor) {
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
