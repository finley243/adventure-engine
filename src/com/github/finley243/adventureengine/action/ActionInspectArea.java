package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.CompleteActionEvent;
import com.github.finley243.adventureengine.event.QueuedEvent;
import com.github.finley243.adventureengine.event.SceneEvent;
import com.github.finley243.adventureengine.menu.MenuChoice;
import com.github.finley243.adventureengine.world.environment.Area;

import java.util.ArrayList;
import java.util.List;

public class ActionInspectArea extends Action {

    private final Area area;

    public ActionInspectArea(Area area) {
        this.area = area;
    }

    @Override
    public void choose(Actor subject, int repeatActionCount) {
        List<QueuedEvent> sceneEvents = new ArrayList<>();
        if (area.getRoom().getDescription() != null) {
            sceneEvents.add(new SceneEvent(area.getRoom().getDescription(), null, new Context(subject.game(), subject, subject)));
            area.getRoom().setKnown();
            for (Area area : area.getRoom().getAreas()) {
                area.setKnown();
            }
        }
        if (area.getDescription() != null) {
            sceneEvents.add(new SceneEvent(area.getDescription(), null, new Context(subject.game(), subject, subject)));
            area.setKnown();
        }
        subject.game().eventQueue().addAllToFront(sceneEvents);
        area.getRoom().triggerScript("on_inspect", subject, subject);
        area.triggerScript("on_inspect", subject, subject);
        subject.onCompleteAction(new CompleteActionEvent(this, repeatActionCount));
    }

    @Override
    public CanChooseResult canChoose(Actor subject) {
        CanChooseResult resultSuper = super.canChoose(subject);
        if (!resultSuper.canChoose()) {
            return resultSuper;
        }
        if (area.getDescription() == null && area.getRoom().getDescription() == null) {
            return new CanChooseResult(false, "Nothing to see");
        }
        return new CanChooseResult(true, null);
    }

    @Override
    public MenuChoice getMenuChoices(Actor subject) {
        return new MenuChoice("Look around", canChoose(subject).canChoose(), new String[]{"look around", "explore"});
    }

}
