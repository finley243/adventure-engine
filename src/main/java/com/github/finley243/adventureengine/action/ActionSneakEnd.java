package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.SensoryEventDispatcher;
import com.github.finley243.adventureengine.menu.action.MenuData;
import com.github.finley243.adventureengine.menu.action.MenuDataSelf;
import com.github.finley243.adventureengine.script.ScriptRuntime;

public class ActionSneakEnd extends Action {

    public ActionSneakEnd(ScriptRuntime scriptRuntime, SensoryEventDispatcher sensoryEventDispatcher) {
        super(scriptRuntime, sensoryEventDispatcher);
    }

    @Override
    public String getID() {
        return "sneak_end";
    }

    @Override
    public Context getContext(Actor subject) {
        return Context.builder().subject(subject).target(subject).build();
    }

    @Override
    public void choose(Actor subject, int repeatActionCount) {
        subject.setSneaking(false);
    }

    @Override
    public CanChooseResult canChoose(Actor subject) {
        CanChooseResult resultSuper = super.canChoose(subject);
        if (!resultSuper.canChoose()) {
            return resultSuper;
        }
        if (subject.isUsingObject()) {
            return new CanChooseResult(false, "Sneaking unavailable");
        }
        return new CanChooseResult(true, null);
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
