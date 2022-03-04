package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ai.ActorTarget;
import com.github.finley243.adventureengine.actor.ai.UtilityUtils;
import com.github.finley243.adventureengine.event.VisualEvent;
import com.github.finley243.adventureengine.menu.MenuData;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Phrases;

public class ActionCrouch extends Action {

    @Override
    public void choose(Actor subject) {
        Context context = new Context(subject);
        subject.game().eventBus().post(new VisualEvent(subject.getArea(), Phrases.get("crouch"), context, this, subject));
        subject.setCrouching(true);
    }

    @Override
    public float utility(Actor subject) {
        return UtilityUtils.getCoverUtility(subject);
    }

    @Override
    public MenuData getMenuData(Actor subject) {
        return new MenuData("Crouch", canChoose(subject));
    }

    @Override
    public int actionPoints() {
        return 1;
    }

}
