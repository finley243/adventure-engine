package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.menu.MenuChoice;
import com.github.finley243.adventureengine.scene.SceneManager;
import com.github.finley243.adventureengine.world.environment.Area;

public class ActionInspectArea extends Action {

    private final Area area;

    public ActionInspectArea(Area area) {
        super(ActionDetectionChance.NONE);
        this.area = area;
    }

    @Override
    public void choose(Actor subject, int repeatActionCount) {
        if (area.getRoom().getDescription() != null) {
            SceneManager.trigger(subject.game(), subject, subject, area.getRoom().getDescription());
            area.getRoom().setKnown();
            for (Area area : area.getRoom().getAreas()) {
                area.setKnown();
            }
        }
        if (area.getDescription() != null) {
            SceneManager.trigger(subject.game(), subject, subject, area.getDescription());
            area.setKnown();
        }
        area.getRoom().triggerScript("on_inspect", subject, subject);
        area.triggerScript("on_inspect", subject, subject);
    }

    @Override
    public boolean canChoose(Actor subject) {
        return super.canChoose(subject) && (area.getDescription() != null || area.getRoom().getDescription() != null);
    }

    @Override
    public MenuChoice getMenuChoices(Actor subject) {
        return new MenuChoice("Look around", canChoose(subject), new String[]{"look around", "explore"});
    }

}
