package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.menu.action.MenuData;
import com.github.finley243.adventureengine.menu.action.MenuDataSelf;

public class ActionSneakStart extends Action {

    @Override
    public String getID() {
        return "sneak_start";
    }

    @Override
    public Context getContext(Actor subject) {
        return new Context(subject.game(), subject, subject);
    }

    @Override
    public void choose(Actor subject, int repeatActionCount) {
        subject.setSneaking(true);
    }

    @Override
    public MenuData getMenuData(Actor subject) {
        return new MenuDataSelf();
    }

    @Override
    public String getPrompt(Actor subject) {
        return "Start Sneaking";
    }

}
