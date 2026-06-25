package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.menu.action.MenuData;
import com.github.finley243.adventureengine.menu.action.MenuDataSelf;

public class ActionSneakStart extends Action {

    public ActionSneakStart(Actor subject, ActionDependencies dependencies) {
        super(subject, dependencies);
    }

    @Override
    public String getID() {
        return "sneak_start";
    }

    @Override
    public Context getContext() {
        return Context.builder().subject(subject).target(subject).build();
    }

    @Override
    public void choose(int repeatActionCount) {
        subject.setSneaking(true);
    }

    @Override
    public CanChooseResult canChoose() {
        CanChooseResult resultSuper = super.canChoose();
        if (!resultSuper.canChoose()) {
            return resultSuper;
        }
        if (subject.isUsingObject()) {
            return new CanChooseResult(false, "Sneaking unavailable");
        }
        return new CanChooseResult(true, null);
    }

    @Override
    public MenuData getMenuData() {
        return new MenuDataSelf();
    }

    @Override
    public String getPrompt() {
        return "Start Sneaking";
    }

}
