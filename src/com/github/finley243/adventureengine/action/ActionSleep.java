package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.MapBuilder;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.CompleteActionEvent;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.menu.MenuChoice;
import com.github.finley243.adventureengine.textgen.TextContext;
import com.github.finley243.adventureengine.textgen.Noun;
import com.github.finley243.adventureengine.textgen.Phrases;

public class ActionSleep extends Action {

    public static final int SLEEP_DURATION = 480;

    public ActionSleep() {}

    @Override
    public void choose(Actor subject, int repeatActionCount) {
        TextContext context = new TextContext(new MapBuilder<String, Noun>().put("actor", subject).build());
        subject.game().eventQueue().addToEnd(new SensoryEvent(subject.getArea(), Phrases.get("sleep"), context, this, null, subject, null));
        subject.startSleep(SLEEP_DURATION);
        subject.endTurn();
        subject.game().eventQueue().addToEnd(new CompleteActionEvent(subject, this, repeatActionCount));
    }

    @Override
    public MenuChoice getMenuChoices(Actor subject) {
        return new MenuChoice("Sleep", canChoose(subject).canChoose(), new String[]{"sleep", "go to sleep", "fall asleep"});
    }

}
