package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.menu.action.MenuData;
import com.github.finley243.adventureengine.menu.action.MenuDataSelf;

public class ActionSneakStart extends Action {

    @Override
    public String getID() {
        return "sneak_start";
    }

    @Override
    public Context getContext(Game game, Actor subject) {
        return Context.builder(game).subject(subject).target(subject).build();
    }

    @Override
    public void choose(Game game, int repeatActionCount, Actor subject) {
        subject.setSneaking(true);
    }

    @Override
    public CanChooseResult canChoose(Game game, Actor subject) {
        CanChooseResult resultSuper = super.canChoose(game, subject);
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
    public String getPrompt(Game game, Actor subject) {
        return "Start Sneaking";
    }

}
