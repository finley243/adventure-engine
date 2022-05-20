package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.menu.MenuData;
import com.github.finley243.adventureengine.scene.SceneManager;
import com.github.finley243.adventureengine.world.environment.Area;

import java.util.List;

public class ActionAreaInspect extends Action {

    private final Area area;

    public ActionAreaInspect(Area area) {
        this.area = area;
    }

    @Override
    public void choose(Actor subject) {
        SceneManager.trigger(subject.game(), List.of(area.getDescription()));
        area.triggerScript("on_inspect", subject);
    }

    @Override
    public MenuData getMenuData(Actor subject) {
        return new MenuData("Look around", canChoose(subject));
    }

}
