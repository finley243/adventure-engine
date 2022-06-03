package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.menu.MenuData;
import com.github.finley243.adventureengine.scene.SceneManager;

public class ActionInspectItem extends Action {

    private final Item item;

    public ActionInspectItem(Item item) {
        this.item = item;
    }

    @Override
    public void choose(Actor subject) {
        SceneManager.trigger(subject.game(), item.getDescription());
        item.triggerScript("on_inspect", subject);
    }

    @Override
    public int actionPoints(Actor subject) {
        return 0;
    }

    @Override
    public MenuData getMenuData(Actor subject) {
        return new MenuData("Inspect", canChoose(subject), new String[]{"inventory", item.getName()});
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof ActionInspectItem)) {
            return false;
        } else {
            ActionInspectItem other = (ActionInspectItem) o;
            return other.item == this.item;
        }
    }

}
