package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.ui.RenderTextEvent;
import com.github.finley243.adventureengine.menu.MenuData;
import com.github.finley243.adventureengine.item.Item;

public class ActionItemInspect extends Action {

    private final Item item;

    public ActionItemInspect(Item item) {
        this.item = item;
    }

    @Override
    public void choose(Actor subject) {
        subject.game().eventBus().post(new RenderTextEvent(item.getDescription()));
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
        if(!(o instanceof ActionItemInspect)) {
            return false;
        } else {
            ActionItemInspect other = (ActionItemInspect) o;
            return other.item == this.item;
        }
    }

}
