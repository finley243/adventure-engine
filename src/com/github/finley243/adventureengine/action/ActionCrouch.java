package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.VisualEvent;
import com.github.finley243.adventureengine.menu.MenuData;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Phrases;

public class ActionCrouch extends Action {

    @Override
    public void choose(Actor subject) {
        Context context = new Context(subject, false);
        Game.EVENT_BUS.post(new VisualEvent(subject.getArea(), Phrases.get("crouch"), context, this, subject));
        subject.setCrouching(true);
    }

    @Override
    public MenuData getMenuData(Actor subject) {
        return new MenuData("Crouch", "Crouch", canChoose(subject));
    }

    @Override
    public int actionPoints() {
        return 0;
    }

}
