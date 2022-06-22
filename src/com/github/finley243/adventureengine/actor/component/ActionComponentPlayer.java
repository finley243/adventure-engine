package com.github.finley243.adventureengine.actor.component;

import com.github.finley243.adventureengine.action.Action;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.menu.MenuManager;

import java.util.List;

public class ActionComponentPlayer implements ActionComponent {

    private final Actor actor;
    // TODO - Global menu manager (could specify in ActionComponentPlayer constructor, store in main Game class)
    private final MenuManager menuManager;

    public ActionComponentPlayer(Actor actor) {
        this.actor = actor;
        this.menuManager = new MenuManager();
        actor.game().eventBus().register(menuManager);
    }

    @Override
    public Action chooseAction(List<Action> actions) {
        return menuManager.actionMenu(actions, actor);
    }

}
