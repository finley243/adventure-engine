package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.VisualEvent;
import com.github.finley243.adventureengine.menu.MenuData;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Phrases;

public class ActionCrouchStop extends Action {

    @Override
    public void choose(Actor subject) {
        subject.setCrouching(false);
        Context context = new Context(subject, false);
        Game.EVENT_BUS.post(new VisualEvent(subject.getArea(), Phrases.get("crouchStop"), context, this, subject));
    }

    @Override
    public float utility(Actor subject) {
        return 0.1f;
    }

    @Override
    public MenuData getMenuData(Actor subject) {
        return new MenuData("Stop crouching", "Stop crouching", canChoose(subject));
    }

    @Override
    public int actionPoints() {
        return 1;
    }
    
}
