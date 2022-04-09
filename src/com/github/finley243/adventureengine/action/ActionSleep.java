package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ai.behavior.Behavior;
import com.github.finley243.adventureengine.menu.MenuData;

public class ActionSleep extends Action {

    public static final int SLEEP_DURATION = 480;

    public ActionSleep() {}

    @Override
    public void choose(Actor subject) {
        subject.startSleep(SLEEP_DURATION);
        subject.endTurn();
    }

    @Override
    public float utility(Actor subject) {
        return 0.0f;
    }

    @Override
    public MenuData getMenuData(Actor subject) {
        return new MenuData("Sleep", canChoose(subject));
    }

}
