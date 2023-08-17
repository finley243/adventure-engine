package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.CompleteActionEvent;
import com.github.finley243.adventureengine.event.SceneEvent;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.menu.action.MenuData;
import com.github.finley243.adventureengine.menu.action.MenuDataInventory;

public class ActionInspectItem extends Action {

    private final Item item;

    public ActionInspectItem(Item item) {
        this.item = item;
    }

    @Override
    public void choose(Actor subject, int repeatActionCount) {
        subject.game().eventQueue().addToEnd(new SceneEvent(item.getDescription(), null, new Context(subject.game(), subject, subject, item)));
        item.triggerScript("on_inspect", subject, subject);
        subject.game().eventQueue().addToEnd(new CompleteActionEvent(subject, this, repeatActionCount));
    }

    @Override
    public int actionPoints(Actor subject) {
        return 0;
    }

    @Override
    public MenuData getMenuData(Actor subject) {
        return new MenuDataInventory(item);
    }

    @Override
    public String getPrompt(Actor subject) {
        return "Inspect";
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ActionInspectItem other)) {
            return false;
        } else {
            return other.item == this.item;
        }
    }

}
