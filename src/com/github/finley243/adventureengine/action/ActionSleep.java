package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.MapBuilder;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.menu.action.MenuData;
import com.github.finley243.adventureengine.menu.action.MenuDataSelf;
import com.github.finley243.adventureengine.textgen.Noun;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.textgen.TextContext;

public class ActionSleep extends Action {

    public static final int SLEEP_DURATION = 480;

    public ActionSleep() {}

    @Override
    public void choose(Actor subject, int repeatActionCount) {
        Context context = new Context(subject.game(), subject, null);
        TextContext textContext = new TextContext(new MapBuilder<String, Noun>().put("actor", subject).build());
        SensoryEvent.execute(subject.game(), new SensoryEvent(subject.getArea(), Phrases.get("sleep"), context, textContext, true, this, null));
        subject.startSleep(SLEEP_DURATION);
        subject.endTurn();
    }

    @Override
    public MenuData getMenuData(Actor subject) {
        return new MenuDataSelf();
    }

    @Override
    public String getPrompt(Actor subject) {
        return "Sleep";
    }

}
