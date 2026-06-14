package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Context;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.event.SensoryEventDispatcher;
import com.github.finley243.adventureengine.item.Item;
import com.github.finley243.adventureengine.menu.action.MenuData;
import com.github.finley243.adventureengine.menu.action.MenuDataInventory;

public class ActionInspectItem extends Action {

    private final Item item;

    public ActionInspectItem(Item item) {
        this.item = item;
    }

    @Override
    public String getID() {
        return "inspect_item";
    }

    @Override
    public Context getContext(Actor subject) {
        return Context.builder().subject(subject).parentItem(item).build();
    }

    @Override
    public void choose(Actor subject, int repeatActionCount, SensoryEventDispatcher sensoryEventDispatcher) {
        if (subject.isPlayer()) {
            item.setKnown();
        }
        game.menuManager().sceneMenu(game, item.getDescription(), Context.builder().subject(subject).target(subject).parentItem(item).build(), false);
        item.triggerScript("on_inspect", game, subject, subject);
    }

    @Override
    public int actionPoints(Actor subject) {
        return 0;
    }

    @Override
    public MenuData getMenuData(Actor subject) {
        return new MenuDataInventory(item, subject.getInventory());
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
