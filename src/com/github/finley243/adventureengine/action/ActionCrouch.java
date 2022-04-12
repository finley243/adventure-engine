package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.NounMapper;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.ai.UtilityUtils;
import com.github.finley243.adventureengine.event.AudioVisualEvent;
import com.github.finley243.adventureengine.menu.MenuData;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Phrases;

public class ActionCrouch extends Action {

    @Override
    public void choose(Actor subject) {
        Context context = new Context(new NounMapper().put("actor", subject).build());
        subject.game().eventBus().post(new AudioVisualEvent(subject.getArea(), Phrases.get("crouch"), context, this, subject));
        subject.setCrouching(true);
    }

    @Override
    public float utility(Actor subject) {
        return 0.0f;
        //return UtilityUtils.getCoverUtility(subject);
    }

    @Override
    public MenuData getMenuData(Actor subject) {
        return new MenuData("Crouch", canChoose(subject));
    }

    @Override
    public int actionPoints(Actor subject) {
        return 1;
    }

}
