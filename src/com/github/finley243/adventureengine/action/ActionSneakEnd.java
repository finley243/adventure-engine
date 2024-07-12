package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.menu.action.MenuData;
import com.github.finley243.adventureengine.menu.action.MenuDataSelf;

public class ActionSneakEnd extends Action {

    @Override
    public String getID() {
        return "sneak_end";
    }

    @Override
    public Context getContext(Actor subject) {
        return new Context(subject.game(), subject, subject);
    }

    @Override
    public void choose(Actor subject, int repeatActionCount) {
        subject.setSneaking(false);
    }

    @Override
    public MenuData getMenuData(Actor subject) {
        return new MenuDataSelf();
    }

    @Override
    public String getPrompt(Actor subject) {
        return "Stop Sneaking";
    }

}
