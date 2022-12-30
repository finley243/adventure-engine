package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.textgen.NounMapper;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.menu.MenuChoice;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.Phrases;

public class ActionSleep extends Action {

    public static final int SLEEP_DURATION = 480;

    public ActionSleep() {
        super(ActionDetectionChance.NONE);
    }

    @Override
    public void choose(Actor subject, int repeatActionCount) {
        Context context = new Context(new NounMapper().put("actor", subject).build());
        subject.game().eventBus().post(new SensoryEvent(subject.getArea(), Phrases.get("sleep"), context, this, null, subject, null));
        subject.startSleep(SLEEP_DURATION);
        subject.endTurn();
    }

    @Override
    public MenuChoice getMenuChoices(Actor subject) {
        return new MenuChoice("Sleep", canChoose(subject), new String[]{"sleep", "go to sleep", "fall asleep"});
    }

}
