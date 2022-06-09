package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.menu.MenuData;
import com.github.finley243.adventureengine.scene.SceneManager;
import com.github.finley243.adventureengine.world.environment.Area;

public class ActionInspectArea extends Action {

    private final Area area;

    public ActionInspectArea(Area area) {
        this.area = area;
    }

    @Override
    public void choose(Actor subject) {
        SceneManager.trigger(subject.game(), subject, area.getDescription());
        area.triggerScript("on_inspect", subject);
    }

    @Override
    public boolean canChoose(Actor subject) {
        return super.canChoose(subject) && area.getDescription() != null;
    }

    @Override
    public MenuData getMenuData(Actor subject) {
        return new MenuData("Look around", canChoose(subject));
    }

}
