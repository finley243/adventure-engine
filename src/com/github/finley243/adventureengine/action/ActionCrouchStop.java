package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.NounMapper;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.AudioVisualEvent;
import com.github.finley243.adventureengine.menu.MenuData;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Phrases;

public class ActionCrouchStop extends Action {

    @Override
    public void choose(Actor subject) {
        subject.setCrouching(false);
        Context context = new Context(new NounMapper().put("actor", subject).build());
        subject.game().eventBus().post(new AudioVisualEvent(subject.getArea(), Phrases.get("crouchStop"), context, this, subject));
    }

    @Override
    public float utility(Actor subject) {
        return 0.0f;
    }

    @Override
    public MenuData getMenuData(Actor subject) {
        return new MenuData("Stop crouching", canChoose(subject));
    }

    @Override
    public int actionPoints(Actor subject) {
        return 1;
    }
    
}
