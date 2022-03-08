package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.event.VisualEvent;
import com.github.finley243.adventureengine.menu.MenuData;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.MultiNoun;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.textgen.Noun;
import com.github.finley243.adventureengine.world.item.Item;

import java.util.ArrayList;
import java.util.List;

public class ActionInventoryTakeAll extends Action {

    private final Noun owner;
    private final Inventory inventory;
    private final List<Item> items;

    public ActionInventoryTakeAll(Noun owner, Inventory inventory, List<Item> items) {
        this.owner = owner;
        this.inventory = inventory;
        this.items = new ArrayList<>(items);
    }

    @Override
    public void choose(Actor subject) {
        for (Item item : items) {
            inventory.removeItem(item);
            subject.inventory().addItem(item);
        }
        Context context = new Context(subject, new MultiNoun(items), owner);
        subject.game().eventBus().post(new VisualEvent(subject.getArea(), Phrases.get("takeFrom"), context, this, subject));
    }

    @Override
    public int actionPoints(Actor subject) {
        return 0;
    }

    @Override
    public MenuData getMenuData(Actor subject) {
        return new MenuData("Take all", canChoose(subject), new String[]{owner.getName(), items.get(0).getName() + inventory.itemCountLabel(items.get(0).getStatsID())});
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof ActionInventoryTakeAll)) {
            return false;
        } else {
            ActionInventoryTakeAll other = (ActionInventoryTakeAll) o;
            return other.owner == this.owner && other.items.equals(this.items) && other.inventory == this.inventory;
        }
    }
    
}
