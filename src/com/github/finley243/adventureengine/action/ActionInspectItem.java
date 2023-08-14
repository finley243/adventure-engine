package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.event.CompleteActionEvent;
import com.github.finley243.adventureengine.event.SceneEvent;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.menu.MenuChoice;

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
    public MenuChoice getMenuChoices(Actor subject) {
        return new MenuChoice("Inspect", canChoose(subject).canChoose(), new String[]{"Inventory", Inventory.getItemNameFormatted(item, subject.getInventory())}, new String[]{"inspect " + item.getName(), "examine " + item.getName(), "look at " + item.getName()});
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
