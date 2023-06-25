package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.menu.MenuChoice;
import com.github.finley243.adventureengine.scene.SceneManager;
import com.github.finley243.adventureengine.world.environment.Area;

public class ActionInspectArea extends Action {

    private final Area area;

    public ActionInspectArea(Area area) {
        this.area = area;
    }

    @Override
    public void choose(Actor subject, int repeatActionCount) {
        if (area.getRoom().getDescription() != null) {
            SceneManager.trigger(new Context(subject.game(), subject, subject), area.getRoom().getDescription());
            area.getRoom().setKnown();
            for (Area area : area.getRoom().getAreas()) {
                area.setKnown();
            }
        }
        if (area.getDescription() != null) {
            SceneManager.trigger(new Context(subject.game(), subject, subject), area.getDescription());
            area.setKnown();
        }
        area.getRoom().triggerScript("on_inspect", subject, subject);
        area.triggerScript("on_inspect", subject, subject);
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
