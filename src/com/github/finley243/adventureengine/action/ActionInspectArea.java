package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.SensoryEvent;
import com.github.finley243.adventureengine.expression.Expression;
import com.github.finley243.adventureengine.menu.action.MenuData;
import com.github.finley243.adventureengine.menu.action.MenuDataArea;
import com.github.finley243.adventureengine.textgen.TextGen;
import com.github.finley243.adventureengine.world.environment.Area;

public class ActionInspectArea extends Action {

    private final Area area;

    public ActionInspectArea(Area area) {
        this.area = area;
    }

    @Override
    public String getID() {
        return "inspect_area";
    }

    @Override
    public Context getContext(Actor subject) {
        return new Context(subject.game(), subject, null, area);
    }

    @Override
    public void choose(Actor subject, int repeatActionCount) {
        if (area.getRoom() != null && area.getRoom().getDescription() != null) {
            subject.game().menuManager().sceneMenu(subject.game(), area.getRoom().getDescription(), new Context(subject.game(), subject, subject), false);
            area.getRoom().setKnown();
            for (Area area : area.getRoom().getAreas()) {
                area.setKnown();
            }
        }
        if (area.getDescription() != null) {
            subject.game().menuManager().sceneMenu(subject.game(), area.getDescription(), new Context(subject.game(), subject, subject), false);
            area.setKnown();
        }
        if (area.getRoom() != null) {
            area.getRoom().triggerScript("on_inspect", subject, subject);
        }
        area.triggerScript("on_inspect", subject, subject);
    }

    @Override
    public CanChooseResult canChoose(Actor subject) {
        CanChooseResult resultSuper = super.canChoose(subject);
        if (!resultSuper.canChoose()) {
            return resultSuper;
        }
        if (area.getDescription() == null && (area.getRoom() == null || area.getRoom().getDescription() == null)) {
            return new CanChooseResult(false, "Nothing to see");
        }
        return new CanChooseResult(true, null);
    }

    @Override
    public int actionPoints(Actor subject) {
        return 0;
    }

    @Override
    public MenuData getMenuData(Actor subject) {
        return new MenuDataArea(area, true);
    }

    @Override
    public String getPrompt(Actor subject) {
        return "Look Around";
    }

}
