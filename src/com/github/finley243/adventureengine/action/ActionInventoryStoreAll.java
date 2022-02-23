package com.github.finley243.adventureengine.action;

import com.github.finley243.adventureengine.Game;
import com.github.finley243.adventureengine.actor.Actor;
import com.github.finley243.adventureengine.actor.Inventory;
import com.github.finley243.adventureengine.event.VisualEvent;
import com.github.finley243.adventureengine.menu.MenuData;
import com.github.finley243.adventureengine.textgen.Context;
import com.github.finley243.adventureengine.textgen.MultiNoun;
import com.github.finley243.adventureengine.textgen.Phrases;
import com.github.finley243.adventureengine.world.Noun;
import com.github.finley243.adventureengine.world.item.Item;

import java.util.ArrayList;
import java.util.List;

public class ActionInventoryStoreAll extends Action {

    private final Noun owner;
    private final Inventory inventory;
    private final List<Item> items;

    public ActionInventoryStoreAll(Noun owner, Inventory inventory, List<Item> items) {
        this.owner = owner;
        this.inventory = inventory;
        this.items = new ArrayList<>(items);
    }

    @Override
    public void choose(Actor subject) {
        for(Item item : items) {
            subject.inventory().removeItem(item);
            inventory.addItem(item);
        }
        Context context = new Context(subject, false, new MultiNoun(items), true, owner, false);
        Game.EVENT_BUS.post(new VisualEvent(subject.getArea(), Phrases.get("storeIn"), context, this, subject));
    }

    @Override
    public int actionPoints() {
        return 0;
    }

    @Override
    public MenuData getMenuData(Actor subject) {
        MultiNoun multiNoun = new MultiNoun(items);
        String fullPrompt = "Put " + multiNoun.getFormattedName(false) + " in " + owner.getFormattedName(false);
        return new MenuData("Store all", fullPrompt, canChoose(subject), new String[]{owner.getName(), "transfer", items.get(0).getName() + subject.inventory().itemCountLabel(items.get(0).getStatsID())});
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof ActionInventoryStoreAll)) {
            return false;
        } else {
            ActionInventoryStoreAll other = (ActionInventoryStoreAll) o;
            return other.owner == this.owner && other.items.equals(this.items) && other.inventory == this.inventory;
        }
    }

}
